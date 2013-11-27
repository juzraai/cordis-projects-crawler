package hu.juranyi.zsolt.cordis.projects;

// TODO JAVADOC: Participant or Coordinator
public class Participant {

	private String address;
	private String administrativeContact;
	private String country;
	private String fax;
	private String name;
	private String tel;
	private String website;

	public String getAddress() {
		return address;
	}

	public String getAdministrativeContact() {
		return administrativeContact;
	}

	public String getCountry() {
		return country;
	}

	public String getFax() {
		return fax;
	}

	public String getName() {
		return name;
	}

	public String getTel() {
		return tel;
	}

	public String getWebsite() {
		return website;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setAdministrativeContact(String administrativeContact) {
		this.administrativeContact = administrativeContact;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

}
