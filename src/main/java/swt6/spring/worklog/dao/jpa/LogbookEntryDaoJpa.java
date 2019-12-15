package swt6.spring.worklog.dao.jpa;

import org.springframework.dao.DataAccessException;
import swt6.spring.worklog.dao.LogbokEntryDao;
import swt6.spring.worklog.domain.Employee;
import swt6.spring.worklog.domain.LogbookEntry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class LogbookEntryDaoJpa implements LogbokEntryDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<LogbookEntry> findByEmployee(Long employeeId) throws DataAccessException {
        return em.createQuery("select le from LogbookEntry le where le.employee.id = :id", LogbookEntry.class).setParameter("id", employeeId).getResultList();
    }

    @Override
    public void deleteById(Long id) throws DataAccessException {
        LogbookEntry lbe = em.find(LogbookEntry.class, id);
        lbe.detachEmployee();
        em.remove(lbe);
    }

    @Override
    public LogbookEntry findById(Long id) {
        return em.find(LogbookEntry.class, id);
    }

    @Override
    public List<LogbookEntry> findAll() {
        return em.createQuery("from LogbookEntry", LogbookEntry.class).getResultList();
    }

    @Override
    public void insert(LogbookEntry entity) {
        em.persist(entity);
    }

    @Override
    public LogbookEntry merge(LogbookEntry entity) {
        return em.merge(entity);
    }
}
