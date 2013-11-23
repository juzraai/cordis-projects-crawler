package hu.juranyi.zsolt.cordis.projects;

import static hu.juranyi.zsolt.common.StringTools.findFirstMatch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectParser {
	private static final Logger LOG = LoggerFactory
			.getLogger(ProjectParser.class);

	public static Project buildProject(String projectDataPage) {
		LOG.info("Parsing project data...");
		Project p = new Project();
		Document doc = Jsoup.parse(projectDataPage);

		Elements els;

		els = doc.select("div.projdet div.box-left");
		String refNoStr = findFirstMatch(els.first().text(),
				"Project reference: (\\d+) ", 1);
		p.setReference(Integer.parseInt(refNoStr));

		// TODO parse data
		return p;
	}

	public static void updatePublications(String publicationsJSON,
			Project project) {
		// TODO parse publication data
	}
}
