package hu.juranyi.zsolt.cordis.projects;

import static hu.juranyi.zsolt.common.StringTools.findFirstMatch;
import hu.juranyi.zsolt.common.JSoupDownloader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	public void all() {
		// TODO log counters i/all
		for (int RCN : fetchRCNs()) {
			byRCN(RCN);
		}
	}

	public void byRCN(int rcn) {
		LOG.info("Downloading project data for RCN: {}", rcn);

		String url = "http://cordis.europa.eu/projects/index.cfm?fuseaction=app.csa&action=read&xslt-template=projects/xsl/projectdet_en.xslt&rcn="
				+ rcn;
		if (null == outputDir || 0 == outputDir.length())
			outputDir = ".";
		String dirname = outputDir + "/";
		String filename = String.format(PROJECT_FILENAME, rcn);
		File file = new File(dirname + filename);
		if (file.exists() && skipExisting) {
			LOG.info("Already downloaded, skipping.");
		} else {
			Document doc = new JSoupDownloader().downloadDocument(url);
			// TODO if (null == doc)
			try {
				new File(dirname).mkdirs();
				FileWriter fw = new FileWriter(file.getAbsolutePath(), false);
				fw.write(doc.html());
				fw.close();
			} catch (IOException ex) {
				LOG.warn("I/O error while writing to file.");
			} // io error
		} // should download

		LOG.info("Downloading publication list for project with RCN: {}", rcn);
		// TODO gen publist file name
		// TODO check if exists, if yes && skipEx -> skip
		// TODO if null == doc -> read file, jsoup.parse -----> doc
		// TODO if null != doc -> parse project ref no -> create link to json
		// http://www.openaire.eu/hu/component/openaire/widget/data/?format=raw&ga=<REF_NO>
		// TODO strip prefix: "openAIREWidgetCallback("
		// TODO strip suffix: ", false);"
		// TODO save JSON file
	}

	public List<Integer> fetchRCNs() {
		List<Integer> rcns = new ArrayList<Integer>();
		int count = fetchProjectCount();
		int start = 1;
		int perpage = 1000;
		do {
			int end = start + perpage - 1;
			LOG.info("Downloading items {}-{}/{}", start, end, count);
			String url = String.format("%s&start=%d&end=%d", BASE_URL, start,
					end);
			Document xml = new JSoupDownloader().downloadDocument(url);
			if (null == xml) {
				LOG.error("Failed fetching items {}-{}.", start, end);
			} else {
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
			}
			start += perpage;
		} while (start < count);
		return rcns;
	}

	public int fetchProjectCount() {
		LOG.info("Retrieving project count...");
		String url = String.format("%s&start=%d&end=%d", BASE_URL, 1, 1);
		Document xml = new JSoupDownloader().downloadDocument(url);
		try {
			Elements els = xml.select("description");
			String countStr = findFirstMatch(els.first().text(),
					"Number of results : \\d+ of (\\d+)", 1);
			int count = Integer.parseInt(countStr);
			LOG.info("There are {} projects.", count);
			return count;
		} catch (Exception ex) {
			LOG.error("Result XML format is corrupt!");
			return -1;
		}
	}

	public ProjectDownloader outputDir(String outputDir) {
		this.outputDir = outputDir;
		return this;
	}

	public ProjectDownloader skipExisting(boolean skipExisting) {
		this.skipExisting = skipExisting;
		return this;
	}
}
