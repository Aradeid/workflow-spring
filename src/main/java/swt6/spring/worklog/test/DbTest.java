package swt6.spring.worklog.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import swt6.spring.worklog.dao.EmployeeDao;
import swt6.spring.worklog.dao.EmployeeRepository;
import swt6.spring.worklog.dao.LogbokEntryDao;
import swt6.spring.worklog.domain.Employee;
import swt6.spring.worklog.domain.LogbookEntry;
import swt6.util.DbScriptRunner;
import swt6.util.JpaUtil;

import static swt6.util.PrintUtil.printSeparator;
import static swt6.util.PrintUtil.printTitle;

public class DbTest {

	private static void createSchema(DataSource ds, String ddlScript) {
		try {
			DbScriptRunner scriptRunner = new DbScriptRunner(ds.getConnection());
			InputStream is = DbTest.class.getClassLoader().getResourceAsStream(ddlScript);
			if (is == null) throw new IllegalArgumentException(
					String.format("File %s not found in classpath.", ddlScript));
			scriptRunner.runScript(new InputStreamReader(is));
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			return;
		}
	}

	private static void testJdbc() {
		try (AbstractApplicationContext factory = new ClassPathXmlApplicationContext(
				"swt6/spring/worklog/test/applicationContext-jdbc.xml")) {

			printTitle("create schema", 60, '-');
			createSchema(factory.getBean("dataSource", DataSource.class),
					"swt6/spring/worklog/test/CreateWorklogDbSchema.sql");

			EmployeeDao emplDao = factory.getBean("emplDaoJdbc", EmployeeDao.class);

			printTitle("save employee", 60, '-');

			Employee empl1 = new Employee("Josefine", "Feichtlbauer", LocalDate.of(1970, 10, 26));
			emplDao.insert(empl1);
			System.out.println(empl1);

			//empl1.setId(null);//creates a second employee
			printTitle("update eployee", 60, '-');
			empl1.setFirstName("Jaquira");
			empl1 = emplDao.merge(empl1);
			System.out.println(empl1);

			printTitle("find eployee", 60, '-');
			Employee empl2 = emplDao.findById(1L);
			System.out.println("empl2 = " + (empl2 == null ? (null) : empl2.toString()));

			Employee empl3 = emplDao.findById(100L);
			System.out.println("empl3 = " + (empl3 == null ? (null) : empl3.toString()));

			printTitle("find all employees", 60, '-');
			Employee empl4 = new Employee("Josef", "Himmerlbauer", LocalDate.of(1968, 12, 3));
			emplDao.findAll().forEach(System.out::println);
		}
	}

	private static void testJpa() {
		try (AbstractApplicationContext factory = new ClassPathXmlApplicationContext(
				"swt6/spring/worklog/test/applicationContext-jpa1.xml")) {

			EntityManagerFactory emFactory = factory.getBean(EntityManagerFactory.class);

			EmployeeDao emplDao = factory.getBean("emplDaoJpa", EmployeeDao.class);

			printTitle("save employee", 60, '-');

			Employee empl1 = new Employee("Josef", "Himmelbauer", LocalDate.of(1950, 1, 1));
			printTitle("insert/update employee", 60, '-');
			try {
				JpaUtil.beginTransaction(emFactory);
				emplDao.insert(empl1);
				empl1.setFirstName("Kevin");
				empl1 = emplDao.merge(empl1);
			} finally {
				JpaUtil.commitTransaction(emFactory);
			}
			printTitle("find employee", 60, '-');
			final Long empl1Id = empl1.getId();
			JpaUtil.executeInTransaction(emFactory, () -> {
				Employee empl = emplDao.findById(empl1Id);
				System.out.println("empl = " + (empl == null ? (null) : empl.toString()));
				empl = emplDao.findById(100L);
				System.out.println("empl = " + (empl == null ? (null) : empl.toString()));
			});

			JpaUtil.executeInTransaction(emFactory, () -> {
				emplDao.findAll().forEach(System.out::println);
			});

			// -----------------------------------------------------------------------

			LogbookEntry entry1 = new LogbookEntry("Analyse",
					LocalDateTime.of(2018, 3, 1, 8, 30), LocalDateTime.of(2018, 3, 1, 16, 15));
			LogbookEntry entry2 = new LogbookEntry("Implementierung",
					LocalDateTime.of(2018, 3, 1, 8, 0), LocalDateTime.of(2018, 3, 1, 17, 30));
			Employee empl2 = new Employee("Valentino", "Hummelbauer", LocalDate.of(1940, 12, 24));

			LogbokEntryDao lbeDao = factory.getBean("lbeDaoJpa", LogbokEntryDao.class);

			Employee finalEmpl = empl1;
			JpaUtil.executeInTransaction(emFactory, () -> {
				entry1.attachEmployee(finalEmpl);
				entry2.attachEmployee(empl2);

				lbeDao.merge(entry1);
				lbeDao.merge(entry2);
			});

			printTitle("findByEmployee", 60, '-');
			JpaUtil.executeInTransaction(emFactory, () -> {
				lbeDao.findByEmployee(finalEmpl.getId()).forEach(System.out::println);
			});

			// ----------------------------------------------------------------------------------
		}
	}

	private static void testSpringData() {
		try (AbstractApplicationContext factory = new ClassPathXmlApplicationContext(
				"swt6/spring/worklog/test/applicationContext-jpa1.xml")) {

			EntityManagerFactory emFactory = factory.getBean(EntityManagerFactory.class);

			JpaUtil.executeInTransaction(emFactory, () -> {
				EmployeeRepository empRepo = JpaUtil.getJpaRepository(emFactory, EmployeeRepository.class);

				Employee empl1 = new Employee("Josef", "Himmelbauer", LocalDate.of(1950, 1, 1));
				Employee empl2 = new Employee("Karl", "Malden", LocalDate.of(1940, 5, 3));

				printTitle("insert employee", 60, '-');
				empl1 = empRepo.save(empl1);
				empl2 = empRepo.save(empl2);
				empRepo.flush();

				printTitle("update employee", 60, '-');
				empl1.setLastName("Himmelbauer-Huber");
				empl1 = empRepo.save(empl1);
			});

			printSeparator(60, '-');

			JpaUtil.executeInTransaction(emFactory, () -> {
				EmployeeRepository empRepo = JpaUtil.getJpaRepository(emFactory, EmployeeRepository.class);
				printTitle("find by id", 60, '-');
				Optional<Employee> empl = empRepo.findById(1L);
				System.out.println("empl = " + (empl.isPresent() ? empl.get() : "<not-found>"));

				printTitle("findAll", 60, '-');
				empRepo.findAll().forEach(System.out::println);

				printTitle("findByLastName", 60, '-');
				Optional<Employee> empl2 = empRepo.findByLastName("Malden");
				if (empl2.isPresent()) System.out.println(empl2);

				printTitle("findByLastNameContaining", 60, '-');
				empRepo.findByLastNameContaining("a").forEach(System.out::println);
			});
		}
	}

	public static void main(String[] args) {
		printSeparator(60);
		printTitle("testJDBC", 60);
		printSeparator(60);
		testJdbc();

		printSeparator(60);
		printTitle("testJpa", 60);
		printSeparator(60);
		//testJpa();

		printSeparator(60);
		printTitle("testSpringData", 60);
		printSeparator(60);
		testSpringData();
	}
}
