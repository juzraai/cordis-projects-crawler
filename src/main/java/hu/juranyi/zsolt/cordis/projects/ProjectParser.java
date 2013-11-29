package hu.juranyi.zsolt.cordis.projects;

import static hu.juranyi.zsolt.common.StringTools.findFirstMatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

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
		els = doc.select("div.projttl h1");
		if (!els.isEmpty()) {
			p.setName(els.first().text());
		}
		if (null == p.getName()) {
			LOG.error("Could not parse project name.");
			// not just warn, it's important!
		}

		// Project title (sub title / long title)
		els = doc.select("div.projttl h2");
		if (!els.isEmpty()) {
			p.setTitle(els.first().text());
		}
		if (null == p.getTitle()) {
			LOG.warn("Could not parse project title.");
		}

		// Project dates
		els = doc.select("div.projdates");
		if (!els.isEmpty()) {
			String text = els.first().text();

			// From
			String datesFrom = findFirstMatch(text,
					"From (\\d{4}-\\d{2}-\\d{2})", 1);
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
			LOG.warn("Could not parse 'from' date.");
		}
		if (null == p.getDatesTo()) {
			LOG.warn("Could not parse 'to' date.");
		}
		if (null == p.getWebsite()) {
			LOG.warn("Webiste link not found.");
		}

		// Objective, General information
		els = doc.select("div.projdescr div.full div.tech");
		if (!els.isEmpty()) {
			String h3Before = "";
			for (Element e : els.first().children()) {
				if (e.tagName().equals("h3")) {
					h3Before = e.text();
				} else if (e.tagName().equals("p")) {
					Document dataDoc = brToOwnDelimiter(e);
					String text = dataDoc.text().replaceAll("#####", "\n");
					if (h3Before.equals("Objective")) {
						p.setObjective(text);
					} else if (h3Before.equals("General Information")) {
						p.setGeneralInformation(text);
					}
				}
			}
		}
		if (null == p.getObjective()) {
			LOG.warn("Could not parse objective.");
		}
		if (null == p.getGeneralInformation()) {
			LOG.warn("Could not parse general information.");
		}

		els = doc.select("div.projdet div.box-left");
		if (!els.isEmpty()) {
			Document dataDoc = brToOwnDelimiter(els.first());
			String text = dataDoc.text();

			// Project reference
			String ref = findFirstMatch(text, "Project reference: (.*?)#####",
					1);
			p.setReference(ref);

			// Project status
			String status = findFirstMatch(text, "Status: (.*?)#####", 1);
			p.setStatus(status.trim());

			// Total cost
			try {
				String line = findFirstMatch(text, "Total cost: (.*?)#####", 1);
				line = line.replaceAll(" ", "");
				String curr = findFirstMatch(line, "([A-Z]+)\\d+", 1);
				String value = findFirstMatch(line, "[A-Z]+(\\d+)", 1);
				p.setCostCurrency(curr);
				p.setCost(Integer.parseInt(value));
			} catch (Exception e) {
			}

			// EU contribution
			try {
				String line = findFirstMatch(text,
						"EU contribution: (.*?)#####", 1);
				line = line.replaceAll(" ", "").trim();
				String curr = findFirstMatch(line, "([A-Z]+)\\d+", 1);
				String value = findFirstMatch(line, "[A-Z]+(\\d+)", 1);
				p.setEuContributionCurrency(curr);
				p.setEuContribution(Integer.parseInt(value));
			} catch (Exception e) {
			}
		}
		if (null == p.getReference()) {
			LOG.error("Could not parse reference number.");
			// not just warn, it's important!
		}
		if (null == p.getStatus()) {
			LOG.warn("Could not parse status.");
		}
		if (null == p.getCostCurrency() || 0 == p.getCost()) {
			LOG.warn("Could not parse total cost.");
		}
		if (null == p.getEuContributionCurrency() || 0 == p.getEuContribution()) {
			LOG.warn("Could not parse EU contribution.");
		}

		els = doc.select("div.projdet div.box-right");
		if (!els.isEmpty()) {
			String text = els.first().text();

			// Programme acronym, Subprogramme area, Contract type
			String progAcronym = findFirstMatch(text,
					"Programme acronym: ([^ ]+?) ", 1);
			String subprogArea = findFirstMatch(text,
					"Subprogramme area: ([^ ]+?) Contract type", 1);
			String contrType = findFirstMatch(text, "Contract type: (.*)", 1);

			p.setProgrammeAcronym(progAcronym);
			p.setSubprogrammeArea(subprogArea);
			p.setContractType(contrType);
		}
		if (null == p.getProgrammeAcronym()) {
			LOG.warn("Could not parse programme acronym.");
		}
		if (null == p.getSubprogrammeArea()) {
			LOG.warn("Could not parse subprogramme area.");
		}
		if (null == p.getContractType()) {
			LOG.warn("Could not parse contract type.");
		}

		// Coordinator
		els = doc.select("div.projcoord");
		if (!els.isEmpty()) {
			p.setCoordinator(parseParticipant(els.first().html()));
			if (els.size() > 1) {
				LOG.warn("There are more than one coordinator!");
			}
		} else {
			LOG.warn("Could not find coordinator.");
		}

		// Participants
		els = doc.select("div.participant");
		if (!els.isEmpty()) {
			List<Participant> participants = new ArrayList<Participant>();
			p.setParticipants(participants);
			for (Element el : els) {
				participants.add(parseParticipant(el.html()));
			}
		} else {
			LOG.warn("Could not find any participants.");
		}

		// Record info
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
			LOG.warn("Could not parse RCN.");
		}
		if (null == p.getLastUpdatedOn()) {
			LOG.warn("Could not parse last updated on.");
		}

		return p;
	}

	private static Document brToOwnDelimiter(Element withBr) {
		// I add an own delimiter, because Jsoup's text() doesn't convert
		// <br/>-s to \n-s.
		String html = withBr.html().replaceAll("<br />", "#####");
		Document withOwnDelimiter = Jsoup.parse(html + "#####");
		return withOwnDelimiter;
	}

	public static Participant parseParticipant(String participantHTML) {
		Participant participant = new Participant();

		Document doc = Jsoup.parse(participantHTML);
		Elements els;

		// Name
		els = doc.select("div.name");
		if (!els.isEmpty()) {
			participant.setName(els.first().text());
		} else {
			LOG.warn("Could not parse participant name.");
		}

		// Country
		els = doc.select("div.country");
		if (!els.isEmpty()) {
			participant.setCountry(els.first().ownText());
		} else {
			LOG.warn("Could not parse participant country.");
		}

		els = doc.select("div.optional.item-content");
		if (!els.isEmpty()) {
			Document dataDoc = brToOwnDelimiter(els.first());
			String text = dataDoc.text(); // need 2 unescape and make UTF8

			// Administrative contact
			String adminCont = findFirstMatch(text,
					"Administrative contact: (.*?)#####", 1);
			if (null != adminCont) {
				participant.setAdministrativeContact(adminCont.trim());
			}

			// Address
			String address = findFirstMatch(text,
					"Administrative contact: .*?#####(.*?)#####", 1);
			if (null != address) {
				participant.setAddress(address.trim());
			}

			// Tel
			String tel = findFirstMatch(text, "Tel:(.*?)#####", 1);
			if (null != tel) {
				participant.setTel(tel.trim());
			}

			// Fax
			String fax = findFirstMatch(text, "Fax:(.*?)#####", 1);
			if (null != fax) {
				participant.setFax(fax.trim());
			}

			// Website
			els = doc.select("a[target=_blank]");
			if (!els.isEmpty()) {
				participant.setWebsite(els.first().attr("href"));
			}
		}
		if (null == participant.getAdministrativeContact()) {
			LOG.warn("Could not parse participant administrative contact.");
		}
		if (null == participant.getAddress()) {
			LOG.warn("Could not parse participant address.");
		}
		// other fields are really optional, they are not everywhere

		return participant;
	}

	public static void updatePublications(String publicationsJSON,
			Project project) {
		LOG.info("Parsing publication list JSON...");

		try {
			Gson gson = new Gson();
			OpenAirePublicationList oapl = gson.fromJson(publicationsJSON,
					OpenAirePublicationList.class);

			if (oapl.getProject().equals(project.getName())) {
				if (null != oapl.getDocs()) {
					LOG.info("Found {} publications.", oapl.getDocs().size());
					project.setPublications(oapl.getDocs());
				} else {
					LOG.warn("Could not find 'docs' array in JSON.");
					project.setPublications(new ArrayList<Publication>());
				}
			} else {
				LOG.error("Project name in JSON ({}) differs from "
						+ "one in Project object ({}). "
						+ "Publication list parsing aborted.",
						oapl.getProject(), project.getName());
			}
		} catch (Exception e) {
			LOG.error("Publication list JSON is invalid: {}", e.getMessage());
		}
	}
}
