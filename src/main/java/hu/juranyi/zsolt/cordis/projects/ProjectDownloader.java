package hu.juranyi.zsolt.cordis.projects;

import static hu.juranyi.zsolt.common.StringTools.findFirstMatch;
import hu.juranyi.zsolt.common.DownloaderEx;
import hu.juranyi.zsolt.common.TextFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fetches one or more projects' data from CORDIS. Can download project data
 * page and publication list JSON string from CORDIS. There are 2 download
 * modes: download all, or download one project by RCN. Download directory and
 * filename templates can be configured. ProjectDownloader can skip already
 * downloaded files, and can load RCNs from already downloaded files in output
 * directory. This is useful when you don't want to fetch the RCN list from
 * CORDIS again, probably you have some missing files, or you want to
 * re-download them again, or want to export (feature coming soon!) downloaded
 * files.<br/>
 * <br/>
 * Example code:
 * 
 * <pre>
 * {@code
 * new ProjectDownloader()
 * 	.outputDir("outputdir/")    // sets output directory
 * 	.projectFilename("%d.html") // sets project data page filename
 * 	.publistFilename("%d.json") // sets publication list JSON filename
 * 	.readRCNsFromDirectory()    // when you don't want to ask CORDIS for the list
 * 	.skipExisting(false)        // turns on re-downloading
 * 	.all(); // .byRCN(90433);
 * }
 * </pre>
 * 
 * @author Zsolt Jurányi
 * 
 */
public class ProjectDownloader {

	// Thanks to https://github.com/ravindraharige/cordis-crawler for the XML
	// download URL!
	private static final String BASE_URL = "http://cordis.europa.eu/newsearch/download.cfm?action=query&collection=EN_PROJ&sort=all&ENGINE_ID=CORDIS_ENGINE_ID&SEARCH_TYPE_ID=CORDIS_SEARCH_ID&typeResp=xml";
	private static final Logger LOG = LoggerFactory
			.getLogger(ProjectDownloader.class);
	private static final String ERR_MSGS = "Server does not respond"
			+ "|The requested record could not be loaded."
			+ "|DocReader returned error code"
			+ "|An unexpected error has occurred. The system administrators have been informed.";

	private int count = -1;
	private String outputDir = "./";
	private String projectFilename = "project-rcn-%d.html";
	private String publistFilename = "project-rcn-%d-pubs.json";
	private List<Integer> rcns;
	private boolean readRCNsFromDirectory;
	private boolean skipExisting = true;

	/**
	 * Calls fetchRCNs(), then calls byRCN() for each RCN in the list.
	 * 
	 * @return List of parsed project data.
	 */
	public List<Project> all() {
		List<Project> projects = new ArrayList<Project>();
		List<Integer> rcns = fetchRCNs();
		for (int i = 0; i < rcns.size(); i++) {
			LOG.info("Fetching project {}/{}, RCN: {}", i + 1, rcns.size(),
					rcns.get(i));
			projects.add(byRCN(rcns.get(i)));
		}
		return projects;
	}

	/**
	 * Downloads or reads project data page and publication list JSON string
	 * then builds a Project object using ProjectParser. It uses fetchContent()
	 * to get files.
	 * 
	 * @param rcn
	 *            RCN of the project you need.
	 * @return Parsed project data or null when something failed.
	 */
	public Project byRCN(int rcn) {
		LOG.info("Fetching project data by RCN: {}", rcn);
		String url = "http://cordis.europa.eu/projects/index.cfm?fuseaction=app.csa&action=read&xslt-template=projects/xsl/projectdet_en.xslt&rcn="
				+ rcn;
		String filename = String.format(projectFilename, rcn);
		String docStr = fetchContent(url, filename, false);
		if (null != docStr) {
			Project project = ProjectParser.buildProject(docStr); // parse
			String ref = project.getReference();
			if (null != project.getReference()) {
				LOG.info("RCN {} <=> Project reference {}", rcn, ref);
				LOG.info("Fetching publication list by project reference: {}",
						ref);
				url = "http://www.openaire.eu/hu/component/openaire/widget/data/?format=raw&ga="
						+ ref;
				filename = String.format(publistFilename, rcn);
				String json = fetchContent(url, filename, true);
				if (null != json) {
					ProjectParser.updatePublications(json, project); // parse
				} else {
					LOG.error("Failed to retrieve JSON. RCN: {}", rcn);
				}
				return project;
			} else { // could not parse ref. no.
				LOG.error("Project data page is corrupt. RCN: {}", rcn);
			}
		} else { // could not download project data page
			LOG.error("Failed to retrieve project data. RCN: {}", rcn);
		}
		return null;
	}

	/**
	 * Downloads from the given URL or reads from the given file, then returns
	 * the content. If skipExisting is false or the file does not exists, it
	 * downloads and saves it to file. Can normalize the JSON string comes from
	 * CORDIS. This method uses DownloaderEx class to download.
	 * 
	 * @param url
	 *            URL to download from.
	 * @param filename
	 *            Filename to read or save the downloaded content.
	 * @param normaliseJSON
	 *            Whether the content is a JSON string needed to be normalised.
	 * @return The downloaded/read content as string.
	 */
	protected String fetchContent(String url, String filename,
			boolean normaliseJSON) {
		String content = null;
		File file = new File(outputDir + filename);
		if (file.exists() && skipExisting) {
			LOG.info("File found ({}), skipping download.", filename);
			TextFile f = new TextFile(file.getAbsolutePath());
			if (f.load()) {
				content = f.getContent();
				// TODO (?) check for server errors in content (?)
				// TODO (?) del files if server error found (?)
				LOG.info("Successfully loaded from file.");
			} else {
				LOG.error("Failed to load from file: {}",
						file.getAbsolutePath());
			}
		} else {
			DownloaderEx d = new DownloaderEx(url);
			d.setServerErrorMessageRegex(ERR_MSGS);
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
				LOG.error("Failed to download. URL: ", url);
			}
		}
		return content;
	}

	/**
	 * Requests a small XML from CORDIS which tells the number of projects in
	 * their database. If readRCNsFromDirectory is true, it reads the output
	 * directory and counts RCN numbers using projectFilename and
	 * publistFilename templates instead of crawling CORDIS. This method works
	 * only when project count not yet fetched. This method uses DownloaderEx
	 * class to download.
	 * 
	 * @return The number of projects will be crawled with the current settings.
	 */
	protected int fetchProjectCount() {
		if (readRCNsFromDirectory) {
			readRCNsFromDirectory();
		} else {
			if (-1 == count) {
				LOG.info("Retrieving project count...");
				String url = String
						.format("%s&start=%d&end=%d", BASE_URL, 1, 1);
				// Document xml = new JSoupDownloader().downloadDocument(url);
				DownloaderEx d = new DownloaderEx(url);
				d.setServerErrorMessageRegex(ERR_MSGS);
				if (d.download()) {
					try {
						Document xml = Jsoup.parse(d.getHtml());
						Elements els = xml.select("description");
						String countStr = findFirstMatch(els.first().text(),
								"Number of results : \\d+ of (\\d+)", 1);
						count = Integer.parseInt(countStr);
						LOG.info("There are {} projects.", count);
					} catch (Exception ex) {
						LOG.error("Result XML format is corrupt!");
					}
				} else {
					LOG.error("Failed to download XML!");
				}
			} // should fetch
		} // XML downloading mode
		return count;
	}

	/**
	 * Crawls CORDIS: downloads search result XMLs, and gathers RCNs from them.
	 * Requests 1000 projects' data in each XML. If readRCNsFromDirectory is
	 * true, it reads the output directory and gathers RCN numbers using
	 * projectFilename and publistFilename templates instead of crawling CORDIS.
	 * This method works only when RCNs not yet fetched. This method uses
	 * DownloaderEx class to download.
	 * 
	 * @return List of gathered RCNs.
	 */
	protected List<Integer> fetchRCNs() {
		if (readRCNsFromDirectory) {
			readRCNsFromDirectory();
		} else {
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
					// Document xml = new
					// JSoupDownloader().downloadDocument(url);
					DownloaderEx d = new DownloaderEx(url);
					d.setServerErrorMessageRegex(ERR_MSGS);
					if (d.download()) {
						Document xml = Jsoup.parse(d.getHtml());
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
								LOG.error("Link format is corrupt: {}", link);
							}
						} // item urls
						LOG.info("Found RCNs so far: {}", rcns.size());
					} else { // failed to download XML
						LOG.error("Failed fetching items {}-{}.", start, end);
					}
					start += perpage;
				} while (start < count);
			} // should fetch
		} // XML downloading mode
		LOG.info("Parsed {} RCNs from XMLs.", rcns.size());
		return rcns;
	}

	/**
	 * 
	 * @param outputDir
	 *            Directory where project data files should be put/found.
	 * @return The current ProjectDownloader object.
	 */
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

	/**
	 * 
	 * @param projectFilename
	 *            Filename template of project data HTML page. Must include a
	 *            '%d' placeholder for RCN.
	 * @return The current ProjectDownloader object.
	 */
	public ProjectDownloader projectFilename(String projectFilename) {
		if (null != projectFilename && projectFilename.length() > 0
				&& projectFilename.indexOf("%d") > -1) {
			this.projectFilename = projectFilename;
		}
		return this;
	}

	/**
	 * 
	 * @param publistFilename
	 *            Filename template of publication list JSON file. Must include
	 *            a '%d' placeholder for RCN.
	 * @return The current ProjectDownloader object.
	 */
	public ProjectDownloader publistFilename(String publistFilename) {
		if (null != publistFilename && publistFilename.length() > 0
				&& publistFilename.indexOf("%d") > -1) {
			this.publistFilename = publistFilename;
		}
		return this;
	}

	/**
	 * Reads the output directory and gathers RCN numbers from the filenames
	 * using projectFilename and publistFilename templates. This method works
	 * only when RCNs not yet fetched.
	 */
	protected void readRCNsFromDirectory() {
		if (-1 == count || null == rcns) {
			LOG.info("Reading RCNs from output directory...");
			rcns = new ArrayList<Integer>();
			File dir = new File(outputDir);
			String dfRegex = projectFilename.replaceFirst("%d", "(\\\\d+)")
					.replaceAll("\\.", "\\\\.");
			String lfRegex = publistFilename.replaceFirst("%d", "(\\\\d+)")
					.replaceAll("\\.", "\\\\.");
			String[] fns = dir.list();
			if (null != fns) {
				for (String fn : fns) {
					if (fn.matches(dfRegex)) {
						String rcnStr = findFirstMatch(fn, dfRegex, 1);
						rcns.add(Integer.parseInt(rcnStr));
					} else if (fn.matches(lfRegex)) {
						String rcnStr = findFirstMatch(fn, lfRegex, 1);
						rcns.add(Integer.parseInt(rcnStr));
					}
				}
			}
			count = rcns.size();
			LOG.info("Found {} RCNs in already downloaded files' names.", count);
		}
	}

	/**
	 * 
	 * @param readRCNsFromDirectory
	 *            If set, ProjectDownloader in 'all' mode will read RCNs from
	 *            already downloaded project pages' filenames (using filename
	 *            templates), instead of crawling CORDIS. This is useful when
	 *            you don't want to fetch the RCN list from CORDIS again,
	 *            probably you have some missing files, or you want to
	 *            re-download them again, or want to export (feature coming
	 *            soon!) downloaded files. Default is FALSE.
	 * @return The current ProjectDownloader object.
	 */
	public ProjectDownloader readRCNsFromDirectory(boolean readRCNsFromDirectory) {
		this.readRCNsFromDirectory = readRCNsFromDirectory;
		return this;
	}

	/**
	 * 
	 * @param skipExisting
	 *            Whether to skip downloading already existing files. Default is
	 *            TRUE.
	 * @return The current ProjectDownloader object.
	 */
	public ProjectDownloader skipExisting(boolean skipExisting) {
		this.skipExisting = skipExisting;
		return this;
	}
}
