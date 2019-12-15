package swt6.spring.worklog.ui;

import swt6.spring.worklog.domain.Employee;
import swt6.spring.worklog.domain.LogbookEntry;
import swt6.spring.worklog.logic.WorkLogFacade;

// UIProcessComponent surrounds all of its methods with a SessionInterceptor
// that keeps the session open. This is important because lazy loading only works
// when an object's session is open. Otherwise you would get a LazyLoadingException
// when child elements are loaded lazily.
public class UIProcessComponent implements UIProcessFacade {

	private WorkLogFacade workLog;

	public void setWorkLog(WorkLogFacade workLog) {
		this.workLog = workLog;
	}

	@Override
	public void saveEmployees(Employee... empls) {
		for (Employee e : empls)
			workLog.syncEmployee(e);
	}

	@Override
	public void findAll() {
		for (Employee e : workLog.findAllEmployees()) {
			System.out.println(e);
			for (LogbookEntry entry : e.getLogbookEntries())
				System.out.println("   " + entry);
		}
	}
}
