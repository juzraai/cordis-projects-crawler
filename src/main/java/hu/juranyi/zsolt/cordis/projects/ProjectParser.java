package hu.juranyi.zsolt.cordis.projects;

import static hu.juranyi.zsolt.common.StringTools.findFirstMatch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO JAVADOC
public class ProjectParser {
	private static final Logger LOG = LoggerFactory
			.getLogger(ProjectParser.class);

	public static Project buildProject(String projectDataPage) {
		LOG.info("Parsing project data...");
		Project p = new Project();
		Document doc = Jsoup.parse(projectDataPage);

		Elements els;

		els = doc.select("div.projdet div.box-left");
		if (!els.isEmpty()) {
			String ref = findFirstMatch(els.first().text(),
					"Project reference: ([^ ]+) ", 1);
			p.setReference(ref);
		}
		if (null == p.getReference()) {
			LOG.error("Could not parse project refrence number.");
		}

		els = doc.select("div#recinfo");
		if (!els.isEmpty()) {
			String rcnStr = findFirstMatch(els.first().text(),
					"Record number: (\\d+) ", 1);
			if (null != rcnStr) {
				p.setRcn(Integer.parseInt(rcnStr));
			}
		}
		if (0 == p.getRcn()) {
			LOG.error("Could not parse RCN.");
		}

		// TODO parse data
		return p;
	}

	public static void updatePublications(String publicationsJSON,
			Project project) {
		// TODO parse publication data
	}
}
