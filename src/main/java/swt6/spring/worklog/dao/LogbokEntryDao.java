package swt6.spring.worklog.dao;

import org.springframework.dao.DataAccessException;
import swt6.spring.worklog.domain.Employee;
import swt6.spring.worklog.domain.LogbookEntry;

import java.util.List;

public interface LogbokEntryDao extends GenericDao<LogbookEntry, Long> {
    List<LogbookEntry> findByEmployee(Long employeeId) throws DataAccessException;
    void deleteById(Long id) throws DataAccessException;
}
