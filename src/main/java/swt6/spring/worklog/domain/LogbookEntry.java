package swt6.spring.worklog.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class LogbookEntry implements Serializable, Comparable<LogbookEntry> {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	private String activity;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
	private Employee employee;

	public LogbookEntry() {
	}

	public LogbookEntry(String activity, LocalDateTime start, LocalDateTime end) {
		this.activity = activity;
		this.startTime = start;
		this.endTime = end;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void attachEmployee(Employee employee) {
		// If this entry is already linked to some employee,
		// remove this link.
		if (this.employee != null)
			this.employee.getLogbookEntries().remove(this);

		// Add a bidirection link between this entry and employee.
		if (employee != null)
			employee.getLogbookEntries().add(this);
		this.employee = employee;
	}

	public void detachEmployee() {
		if (this.employee != null)
			this.employee.getLogbookEntries().remove(this);

		this.employee = null;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime end) {
		this.endTime = end;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime start) {
		this.startTime = start;
	}

	@Override
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		return activity + ": "
				+ startTime.format(formatter) + " - "
				+ endTime.format(formatter) + " ("
				+ getEmployee().getLastName() + ")";
	}

	public int compareTo(LogbookEntry entry) {
		return this.startTime.compareTo(entry.startTime);
	}
}
