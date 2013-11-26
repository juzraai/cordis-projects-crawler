package hu.juranyi.zsolt.cordis.projects;

import static hu.juranyi.zsolt.common.StringTools.findFirstMatch;

import java.text.SimpleDateFormat;

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
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Elements els;

		// Project name (main title / short title)
		els = doc.select("h1");
		if (!els.isEmpty()) {
			p.setName(els.first().text());
		}
		if (null == p.getName()) {
			LOG.error("Could not parse project name.");
		}

		// Project title (sub title / long title)
		els = doc.select("h2");
		if (!els.isEmpty()) {
			p.setTitle(els.first().text());
		}
		if (null == p.getTitle()) {
			LOG.error("Could not parse project title.");
		}

		// Project dates
		els = doc.select("div.projdates");
		if (!els.isEmpty()) {
			String text = els.first().text();

			// From
			String datesFrom = findFirstMatch(text,
					"From (\\d{4}-\\d{2}-\\d{2}) ", 1);
			try {
				p.setDatesFrom(dateFormat.parse(datesFrom));
			} catch (Exception e) {
			}

			// To
			String datesTo = findFirstMatch(text, " to (\\d{4}-\\d{2}-\\d{2})",
					1);
			try {
				p.setDatesTo(dateFormat.parse(datesTo));
			} catch (Exception e) {
			}

			// Website
			els = els.select("a");
			if (!els.isEmpty()) {
				p.setWebsite(els.first().attr("href"));
			}
		}
		if (null == p.getDatesFrom()) {
			LOG.error("Could not parse 'from' date.");
		}
		if (null == p.getDatesTo()) {
			LOG.error("Could not parse 'to' date.");
		}
		if (null == p.getWebsite()) {
			LOG.warn("Webiste link not found.");
		}

		// Objective
		els = doc.select("div.projdescr div.full div.tech p");
		if (!els.isEmpty()) {
			p.setObjective(els.first().text());
			// TODO maybe we should handle <br/> tags
		}
		if (null == p.getObjective()) {
			LOG.error("Could not parse objective.");
		}

		els = doc.select("div.projdet div.box-left");
		if (!els.isEmpty()) {
			String text = els.first().text();

			// Project reference
			String ref = findFirstMatch(text, "Project reference: ([^ ]+) ", 1);
			p.setReference(ref);

			// Project status
			String status = findFirstMatch(text, "Status: (.*?) Total cost", 1);
			p.setStatus(status.trim());

			// Total cost
			try {
				String line = findFirstMatch(text,
						"Total cost: (.*?) EU contribution", 1);
				line = line.replaceAll(" ", "");
				String curr = findFirstMatch(line, "([A-Z]+)\\d+", 1);
				String value = findFirstMatch(line, "[A-Z]+(\\d+)", 1);
				p.setCostCurrency(curr);
				p.setCost(Integer.parseInt(value));
			} catch (Exception e) {
			}

			// EU contribution
			try {
				String line = findFirstMatch(text, "EU contribution: (.*)", 1);
				line = line.replaceAll(" ", "").trim();
				String curr = findFirstMatch(line, "([A-Z]+)\\d+", 1);
				String value = findFirstMatch(line, "[A-Z]+(\\d+)", 1);
				p.setEuContributionCurrency(curr);
				p.setEuContribution(Integer.parseInt(value));
			} catch (Exception e) {
			}
		}
		if (null == p.getReference()) {
			LOG.error("Could not parse refrence number.");
		}
		if (null == p.getStatus()) {
			LOG.error("Could not parse status.");
		}
		if (null == p.getCostCurrency() || 0 == p.getCost()) {
			LOG.error("Could not parse total cost.");
		}
		if (null == p.getEuContributionCurrency() || 0 == p.getEuContribution()) {
			LOG.error("Could not parse EU contribution.");
		}

		els = doc.select("div.projdet div.box-right");
		if (!els.isEmpty()) {
			String text = els.first().text();

			// Programme acronym, Subprogramme area, Contract type
			String progAcronym = findFirstMatch(text,
					"Programme acronym: ([^ ]+?) Subprogramme area", 1);
			String subprogArea = findFirstMatch(text,
					"Subprogramme area: ([^ ]+?) Contract type", 1);
			String contrType = findFirstMatch(text, "Contract type: (.*)", 1);
			p.setProgrammeAcronym(progAcronym);
			p.setSubprogrammeArea(subprogArea);
			p.setContractType(contrType);
		}
		if (null == p.getProgrammeAcronym()) {
			LOG.error("Could not parse programme acronym.");
		}
		if (null == p.getSubprogrammeArea()) {
			LOG.error("Could not parse subprogramme area.");
		}
		if (null == p.getContractType()) {
			LOG.error("Could not parse contract type.");
		}

		els = doc.select("div#recinfo");
		if (!els.isEmpty()) {
			String text = els.first().text();

			// Record number (RCN)
			String rcnStr = findFirstMatch(text, "Record number: (\\d+) ", 1);
			if (null != rcnStr) {
				p.setRcn(Integer.parseInt(rcnStr));
			}

			// Last updated
			String lastUpdated = findFirstMatch(text,
					"Last updated on \\(QVD\\): (\\d{4}-\\d{2}-\\d{2})", 1);
			try {
				p.setLastUpdatedOn(dateFormat.parse(lastUpdated));
			} catch (Exception e) {
			}
		}
		if (0 == p.getRcn()) {
			LOG.error("Could not parse RCN.");
		}
		if (null == p.getLastUpdatedOn()) {
			LOG.error("Could not parse last updated on.");
		}

		// TODO parse data
		return p;
	}

	public static void updatePublications(String publicationsJSON,
			Project project) {
		// TODO parse publication data
	}
}
