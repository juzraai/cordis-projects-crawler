package hu.juranyi.zsolt.cordis.projects;

import static hu.juranyi.zsolt.common.StringTools.findFirstMatch;
import hu.juranyi.zsolt.common.Downloader;
import hu.juranyi.zsolt.common.DownloaderEx;
import hu.juranyi.zsolt.common.JSoupDownloader;
import hu.juranyi.zsolt.common.TextFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO JAVADOC
// TODO CODE COMMENTS
// Thanks to https://github.com/ravindraharige/cordis-crawler for the XML download URL!
public class ProjectDownloader {

	private static final String BASE_URL = "http://cordis.europa.eu/newsearch/download.cfm?action=query&collection=EN_PROJ&sort=all&ENGINE_ID=CORDIS_ENGINE_ID&SEARCH_TYPE_ID=CORDIS_SEARCH_ID&typeResp=xml";
	private static final Logger LOG = LoggerFactory
			.getLogger(ProjectDownloader.class);

	private int count = -1;
	private String outputDir = "./";
	private String projectFilename = "project-rcn-%d.html";
	private String publistFilename = "project-rcn-%d-pubs.json";
	private List<Integer> rcns;
	private boolean skipExisting = true;

	public List<Project> all() {
		List<Project> projects = new ArrayList<Project>();
		// TODO log counters i/all
		for (int RCN : fetchRCNs()) {
			projects.add(byRCN(RCN));
		}
		return projects;
	}

	public Project byRCN(int rcn) {
		LOG.info("Fetching project data by RCN: {}", rcn);
		String url = "http://cordis.europa.eu/projects/index.cfm?fuseaction=app.csa&action=read&xslt-template=projects/xsl/projectdet_en.xslt&rcn="
				+ rcn;
		String filename = String.format(projectFilename, rcn);
		String docStr = fetchContent(url, filename, false);
		if (null != docStr) {
			Project project = ProjectParser.buildProject(docStr);
			String refNoStr = Integer.toString(project.getReference());
			if (project.getReference() > 0) {
				LOG.info("RCN {} <=> Project reference {}", rcn, refNoStr);
				LOG.info("Fetching publication list by project reference: {}",
						refNoStr);
				url = "http://www.openaire.eu/hu/component/openaire/widget/data/?format=raw&ga="
						+ refNoStr;
				filename = String.format(publistFilename, rcn);
				String json = fetchContent(url, filename, true);
				ProjectParser.updatePublications(json, project);
				return project;
			} else {
				LOG.error("Project data page is corrupt.");
			}
		} else {
			LOG.error("Failed to retrieve project data.");
		}
		return null;
	}

	public String fetchContent(String url, String filename,
			boolean normaliseJSON) {
		String content = null;
		File file = new File(outputDir + filename);
		if (file.exists() && skipExisting) {
			LOG.info("File found ({}), skipping download.", filename);
			TextFile f = new TextFile(file.getAbsolutePath());
			if (f.load()) {
				content = f.getContent();
				LOG.info("Successfully loaded from file.");
			} else {
				LOG.error("Failed to load from file: {}",
						file.getAbsolutePath());
			}
		} else {
			Downloader d = new DownloaderEx(url);
			if (d.download()) {
				content = d.getHtml();
				LOG.info("Successfully downloaded.");
				if (normaliseJSON) {
					if (!content.startsWith("{"))
						content = content.substring(content.indexOf('{'));
					if (!content.endsWith("}"))
						content = content.substring(0,
								content.lastIndexOf('}') + 1);
					content = StringEscapeUtils.unescapeJava(content);
				}
				new File(outputDir).mkdirs();
				TextFile f = new TextFile(file.getAbsolutePath());
				f.setContent(content);
				if (f.save()) {
					LOG.info("Successfully saved to file ({}).", filename);
				} else {
					LOG.error("Failed to save to file: {}",
							file.getAbsolutePath());
				}
			} else {
				LOG.error("Failed to download.");
			}
		}
		return content;
	}

	public int fetchProjectCount() {
		if (-1 == count) {
			LOG.info("Retrieving project count...");
			String url = String.format("%s&start=%d&end=%d", BASE_URL, 1, 1);
			Document xml = new JSoupDownloader().downloadDocument(url);
			try {
				Elements els = xml.select("description");
				String countStr = findFirstMatch(els.first().text(),
						"Number of results : \\d+ of (\\d+)", 1);
				int count = Integer.parseInt(countStr);
				LOG.info("There are {} projects.", count);
			} catch (Exception ex) {
				LOG.error("Result XML format is corrupt!");
			}
		}
		return count;
	}

	public List<Integer> fetchRCNs() {
		if (null == rcns) {
			rcns = new ArrayList<Integer>();
			int count = fetchProjectCount();
			int start = 1;
			int perpage = 1000;
			do {
				int end = start + perpage - 1;
				LOG.info("Fetching items {}-{}/{}", start, end, count);
				String url = String.format("%s&start=%d&end=%d", BASE_URL,
						start, end);
				Document xml = new JSoupDownloader().downloadDocument(url);
				if (null != xml) {
					for (Element linkElement : xml.select("item url")) {
						String link = linkElement.text();
						String rcnStr = findFirstMatch(link,
								"projects/rcn/(\\d+)_", 1);
						try {
							int rcn = Integer.parseInt(rcnStr);
							if (!rcns.contains(rcn)) {
								rcns.add(rcn);
							}
						} catch (Exception ex) {
							LOG.warn("Link format is corrupt: {}", link);
						}
					}
				} else {
					LOG.error("Failed fetching items {}-{}.", start, end);
				}
				start += perpage;
			} while (start < count);
		}
		return rcns;
	}

	public ProjectDownloader outputDir(String outputDir) {
		if (null == outputDir || 0 == outputDir.length()) {
			outputDir = ".";
		}
		if (!outputDir.endsWith("/")) {
			outputDir += "/";
		}
		this.outputDir = outputDir;
		return this;
	}

	public ProjectDownloader projectFilename(String projectFilename) {
		if (null != projectFilename && projectFilename.length() > 0
				&& projectFilename.indexOf("%d") > -1) {
			this.projectFilename = projectFilename;
		}
		return this;
	}

	public ProjectDownloader publistFilename(String publistFilename) {
		if (null != publistFilename && publistFilename.length() > 0
				&& publistFilename.indexOf("%d") > -1) {
			this.publistFilename = publistFilename;
		}
		return this;
	}

	public ProjectDownloader skipExisting(boolean skipExisting) {
		this.skipExisting = skipExisting;
		return this;
	}
}
