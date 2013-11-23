package hu.juranyi.zsolt.cordis.projects;

import static hu.juranyi.zsolt.common.StringTools.findFirstMatch;
import hu.juranyi.zsolt.common.Downloader;
import hu.juranyi.zsolt.common.JSoupDownloader;
import hu.juranyi.zsolt.common.TextFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Thanks to https://github.com/ravindraharige/cordis-crawler for the XML download URL!
public class ProjectDownloader {

	private static final String BASE_URL = "http://cordis.europa.eu/newsearch/download.cfm?action=query&collection=EN_PROJ&sort=all&ENGINE_ID=CORDIS_ENGINE_ID&SEARCH_TYPE_ID=CORDIS_SEARCH_ID&typeResp=xml";
	private static final String PROJECT_FILENAME = "project-rcn-%d.html";
	private static final String PUBLIST_FILENAME = "project-rcn-%d-pubs.json";
	private static final Logger LOG = LoggerFactory
			.getLogger(ProjectDownloader.class);

	private String outputDir;
	private boolean skipExisting = true;
	private int count = -1;
	private List<Integer> rcns;

	public void all() {
		// TODO log counters i/all
		for (int RCN : fetchRCNs()) {
			byRCN(RCN);
		}
	}

	public void byRCN(int rcn) {
		LOG.info("Fetching project data by RCN: {}", rcn);

		String url = "http://cordis.europa.eu/projects/index.cfm?fuseaction=app.csa&action=read&xslt-template=projects/xsl/projectdet_en.xslt&rcn="
				+ rcn;

		String filename = String.format(PROJECT_FILENAME, rcn);
		String docStr = fetchContent(url, filename, false);
		if (null != docStr) {
			LOG.info("Fetching publication list for project with RCN: {}", rcn);
			Document doc = Jsoup.parse(docStr);
			Elements els = doc.select("div.projdet div.box-left");
			try {
				String refNoStr = findFirstMatch(els.first().text(),
						"Project reference: (\\d+) ", 1);
				url = "http://www.openaire.eu/hu/component/openaire/widget/data/?format=raw&ga="
						+ refNoStr;
				filename = String.format(PUBLIST_FILENAME, rcn);
				fetchContent(url, filename, true);
			} catch (Exception ex) {
				LOG.error("Project data page is corrupt.");
			}
		} else {
			LOG.error("Failed to retrieve project data.");
		}
	}

	public String fetchContent(String url, String filename, boolean normaliseJSON) {
		String content = null;
		File file = new File(outputDir + filename);
		if (file.exists() && skipExisting) {
			LOG.info("File found, skipping download.");
			TextFile f = new TextFile(file.getAbsolutePath());
			if (f.load()) {
				content = f.getContent();
				LOG.info("Successfully loaded from file.");
			} else {
				LOG.error("Failed to load from file: {}",
						file.getAbsolutePath());
			}
		} else {
			Downloader d = new Downloader(url);
			d.download(10);
			content = d.getHtml();
			if (null != content) {
				LOG.info("Successfully downloaded.");
				if (normaliseJSON) {
					if (!content.startsWith("{"))
						content = content.substring(content.indexOf('{'));
					if (!content.endsWith("}"))
						content = content.substring(0,
								content.lastIndexOf('}') + 1);
					content = content.replaceAll("\\\\n", "");
					content = content.replaceAll("\\\\/", "");
				}
				new File(outputDir).mkdirs();
				TextFile f = new TextFile(file.getAbsolutePath());
				f.setContent(content);
				if (!f.save()) {
					LOG.error("Failed to save to file: {}",
							file.getAbsolutePath());
				}
			} else {
				LOG.error("Failed to download.");
			}
		}
		return content;
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
					} // links on page
				} else {
					LOG.error("Failed fetching items {}-{}.", start, end);
				}
				start += perpage;
			} while (start < count);
		}
		return rcns;
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

	public ProjectDownloader skipExisting(boolean skipExisting) {
		this.skipExisting = skipExisting;
		return this;
	}
}
