package hu.juranyi.zsolt.cordis.projects;

import java.util.Date;

//TODO JAVADOC
public class Project {
	// TODO bean with project properties, getters, setters

	private int rcn;
	private String reference;
	private Date lastUpdated;

	// private List<Publication> publications;
	public int getRcn() {
		return rcn;
	}

	public void setRcn(int rcn) {
		this.rcn = rcn;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

}
