package hu.juranyi.zsolt.cordis.projects;

import java.util.Date;
import java.util.List;

//TODO JAVADOC
public class Project {

	private String contractType;
	private Participant coordinator; // TODO It's sure there's only one?!
	private int cost;
	private String costCurrency;
	private Date datesFrom;
	private Date datesTo;
	private int euContribution;
	private String euContributionCurrency;
	private String generalInformation;
	private Date lastUpdatedOn;
	private String name;
	private String objective;
	private List<Participant> participants;
	private String programmeAcronym;
	private List<Publication> publications;
	private int rcn;
	private String reference;
	private String status;
	private String subprogrammeArea;
	private String title;
	private String website;

	public String getContractType() {
		return contractType;
	}

	public Participant getCoordinator() {
		return coordinator;
	}

	public int getCost() {
		return cost;
	}

	public String getCostCurrency() {
		return costCurrency;
	}

	public Date getDatesFrom() {
		return datesFrom;
	}

	public Date getDatesTo() {
		return datesTo;
	}

	public int getEuContribution() {
		return euContribution;
	}

	public String getEuContributionCurrency() {
		return euContributionCurrency;
	}

	public String getGeneralInformation() {
		return generalInformation;
	}

	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public String getName() {
		return name;
	}

	public String getObjective() {
		return objective;
	}

	public List<Participant> getParticipants() {
		return participants;
	}

	public String getProgrammeAcronym() {
		return programmeAcronym;
	}

	public List<Publication> getPublications() {
		return publications;
	}

	public int getRcn() {
		return rcn;
	}

	public String getReference() {
		return reference;
	}

	public String getStatus() {
		return status;
	}

	public String getSubprogrammeArea() {
		return subprogrammeArea;
	}

	public String getTitle() {
		return title;
	}

	public String getWebsite() {
		return website;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public void setCoordinator(Participant coordinator) {
		this.coordinator = coordinator;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public void setCostCurrency(String costCurrency) {
		this.costCurrency = costCurrency;
	}

	public void setDatesFrom(Date datesFrom) {
		this.datesFrom = datesFrom;
	}

	public void setDatesTo(Date datesTo) {
		this.datesTo = datesTo;
	}

	public void setEuContribution(int euContribution) {
		this.euContribution = euContribution;
	}

	public void setEuContributionCurrency(String euContributionCurrency) {
		this.euContributionCurrency = euContributionCurrency;
	}

	public void setGeneralInformation(String generalInformation) {
		this.generalInformation = generalInformation;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public void setParticipants(List<Participant> participants) {
		this.participants = participants;
	}

	public void setProgrammeAcronym(String programmeAcronym) {
		this.programmeAcronym = programmeAcronym;
	}

	public void setPublications(List<Publication> publications) {
		this.publications = publications;
	}

	public void setRcn(int rcn) {
		this.rcn = rcn;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setSubprogrammeArea(String subprogrammeArea) {
		this.subprogrammeArea = subprogrammeArea;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setWebsite(String website) {
		this.website = website;
	}
}
