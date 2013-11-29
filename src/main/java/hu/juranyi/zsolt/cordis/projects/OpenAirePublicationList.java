package hu.juranyi.zsolt.cordis.projects;

import java.util.List;

// TODO JAVADOC
public class OpenAirePublicationList {

	private List<Publication> docs;
	private String openaireImageUrl;
	private String project;

	public List<Publication> getDocs() {
		return docs;
	}

	public String getOpenaireImageUrl() {
		return openaireImageUrl;
	}

	public String getProject() {
		return project;
	}

	public void setDocs(List<Publication> docs) {
		this.docs = docs;
	}

	public void setOpenaireImageUrl(String openaireImageUrl) {
		this.openaireImageUrl = openaireImageUrl;
	}

	public void setProject(String project) {
		this.project = project;
	}

}
