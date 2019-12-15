package swt6.spring.worklog.logic;

import org.springframework.transaction.annotation.Transactional;
import swt6.spring.worklog.dao.EmployeeDao;
import swt6.spring.worklog.dao.LogbokEntryDao;
import swt6.spring.worklog.domain.Employee;
import swt6.spring.worklog.domain.LogbookEntry;

import java.util.List;

@Transactional
public class WorklogImpl1 implements WorkLogFacade {
    private EmployeeDao emplDao;
    private LogbokEntryDao entryDao;

    public void setEmplDao(EmployeeDao emplDao) {
        this.emplDao = emplDao;
    }

    public void setEntryDao(LogbokEntryDao entryDao) {
        this.entryDao = entryDao;
    }

    @Override
    public Employee syncEmployee(Employee employee) {
        return emplDao.merge(employee);
    }

    @Transactional(readOnly = true)
    @Override
    public Employee findEmployeeById(Long id) {
        return emplDao.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Employee> findAllEmployees() {
        return emplDao.findAll();
    }

    @Override
    public LogbookEntry syncLogbookEntry(LogbookEntry entry) {
        return entryDao.merge(entry);
    }

    @Transactional(readOnly = true)
    @Override
    public LogbookEntry findLogbookEntryById(Long id) {
        return entryDao.findById(id);
    }

    @Override
    public void deleteLogbookEntryById(Long id) {
        entryDao.deleteById(id);
    }
}
