package hu.juranyi.zsolt.cordis.projects;

import java.util.List;

public class Publication {

	private List<String> authors;
	private String title;
	private String url;

	public List<String> getAuthors() {
		return authors;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
