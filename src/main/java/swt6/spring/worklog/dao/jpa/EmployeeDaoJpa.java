package swt6.spring.worklog.dao.jpa;

import swt6.spring.worklog.dao.EmployeeDao;
import swt6.spring.worklog.domain.Employee;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class EmployeeDaoJpa implements EmployeeDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Employee findById(Long id) {
        return em.find(Employee.class, id);
    }

    @Override
    public List<Employee> findAll() {
        return em.createQuery("from Employee", Employee.class).getResultList();
    }

    @Override
    public void insert(Employee entity) {
        em.persist(entity);
    }

    @Override
    public Employee merge(Employee entity) {
        return em.merge(entity);
    }
}
