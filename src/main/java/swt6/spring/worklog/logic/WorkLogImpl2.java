package swt6.spring.worklog.logic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swt6.spring.worklog.dao.EmployeeRepository;
import swt6.spring.worklog.dao.LogbookEntryRepository;
import swt6.spring.worklog.domain.Employee;
import swt6.spring.worklog.domain.LogbookEntry;

// requires <tx:annotation-driven /> element in config file
@Service("workLog")
@Transactional
public class WorkLogImpl2 implements WorkLogFacade {

  @Autowired
  private EmployeeRepository      employeeRepo;

  @Autowired
  private LogbookEntryRepository logbookEntryRepo;
  
  public WorkLogImpl2() {
  }
  
  //================ Business logic method for Employee ================

  @Transactional(readOnly = true)
  public Employee findEmployeeById(Long id) {
    return employeeRepo.findById(id).orElse(null);
  }

  @Transactional(readOnly = true)
  public List<Employee> findAllEmployees() {
    return employeeRepo.findAll();
  }

  public Employee syncEmployee(Employee employee) {
    return employeeRepo.saveAndFlush(employee);
  }

  // ============== Business logic method for LogbookEntry ================
  public LogbookEntry syncLogbookEntry(LogbookEntry entry) {
    return logbookEntryRepo.saveAndFlush(entry);
  }

  @Transactional(readOnly = true)
  public LogbookEntry findLogbookEntryById(Long id) {
    return logbookEntryRepo.findById(id).orElse(null);
  }

  public void deleteLogbookEntryById(Long id) {
    LogbookEntry entry = logbookEntryRepo.findById(id).orElse(null);
    if (entry != null) {
	    entry.detachEmployee();
	    logbookEntryRepo.delete(entry); // not necessary if orphanRemoval=true
    }
  }
}
