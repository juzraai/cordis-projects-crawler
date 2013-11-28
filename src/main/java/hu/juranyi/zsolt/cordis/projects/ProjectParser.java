package hu.juranyi.zsolt.cordis.projects;

import static hu.juranyi.zsolt.common.StringTools.findFirstMatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
		}

		// Project title (sub title / long title)
		els = doc.select("div.projttl h2");
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
			// I add an own delimiter, because Jsoup's text() doesn't convert
			// <br/>-s to \n-s.
			String html = els.first().html().replaceAll("<br />", "#####");
			String text = Jsoup.parse(html).text().replaceAll("#####", "\n");
			p.setObjective(text);
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
			// TODO FIX http://cordis.europa.eu/projects/rcn/19535_en.html
			// TODO convert it to text with line breaks like in participants

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
			// TODO FIX http://cordis.europa.eu/projects/rcn/31599_en.html
			// TODO convert it to text with line breaks like in participants

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

		// Coordinator
		els = doc.select("div.projcoord");
		if (!els.isEmpty()) {
			p.setCoordinator(parseParticipant(els.first().html()));
			if (els.size() > 1) {
				LOG.warn("There are more than one coordinator!");
			}
		} else {
			LOG.error("Could not find coordinator.");
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
			LOG.error("Could not find any participants.");
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
			LOG.error("Could not parse RCN.");
		}
		if (null == p.getLastUpdatedOn()) {
			LOG.error("Could not parse last updated on.");
		}

		return p;
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
			LOG.error("Could not parse participant name.");
		}

		// Country
		els = doc.select("div.country");
		if (!els.isEmpty()) {
			participant.setCountry(els.first().ownText());
		} else {
			LOG.error("Could not parse participant country.");
		}

		els = doc.select("div.optional.item-content");
		if (!els.isEmpty()) {
			// I add an own delimiter, because Jsoup's text() doesn't convert
			// <br/>-s to \n-s.
			String html = els.first().html().replaceAll("<br />", "#####");
			Document dataDoc = Jsoup.parse(html + "#####");
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
			LOG.error("Could not parse participant administrative contact.");
		}
		if (null == participant.getAddress()) {
			LOG.error("Could not parse participant address.");
		}
		// other fields are really optional, they are not everywhere

		return participant;
	}

	public static void updatePublications(String publicationsJSON,
			Project project) {
		LOG.info("Parsing publication list JSON...");

		try {
			JsonElement el = new JsonParser().parse(publicationsJSON);
			// parse throws exception for invalid JSON

			JsonObject root = el.getAsJsonObject();

			// Project name
			JsonElement projectNameEl = root.get("project");
			if (null != projectNameEl && projectNameEl.isJsonPrimitive()) {
				String projectName = projectNameEl.getAsString();
				if (!project.getName().equals(projectName)) {
					LOG.error("Project name in JSON ({}) differs from "
							+ "one in Project object ({}). "
							+ "Publication list parsing aborted.", projectName,
							project.getName());
					return;
				}
			} else {
				LOG.error("Could not parse project name from JSON. "
						+ "Publication list parsing aborted.");
				return;
			}

			// Publications
			JsonElement pubsEl = root.get("docs");
			if (null != pubsEl && pubsEl.isJsonArray()) {
				List<Publication> publications = new ArrayList<Publication>();
				project.setPublications(publications);

				JsonArray pubsArr = root.getAsJsonArray("docs");
				Iterator<JsonElement> pubsIt = pubsArr.iterator();
				while (pubsIt.hasNext()) {
					JsonObject pubEl = pubsIt.next().getAsJsonObject();

					Publication publication = new Publication();
					publications.add(publication);

					// Publication title
					JsonElement title = pubEl.get("title");
					if (null != title && !title.isJsonNull()) {
						publication.setTitle(title.getAsString()
								.replaceAll("\\n", " ").replaceAll(" +", " ")
								.trim());
					} else {
						LOG.error("Could not parse publication title.");
					}

					// Publication URL
					JsonElement url = pubEl.get("url");
					if (null != url && !url.isJsonNull()) {
						publication.setUrl(url.getAsString());
					}

					// Publication authors
					JsonElement ausEl = pubEl.get("authors");
					if (null != ausEl && ausEl.isJsonArray()) {
						List<String> authors = new ArrayList<String>();
						publication.setAuthors(authors);

						JsonArray ausArr = ausEl.getAsJsonArray();
						Iterator<JsonElement> auIt = ausArr.iterator();
						while (auIt.hasNext()) {
							JsonElement au = auIt.next();
							authors.add(au.getAsString());
						} // authors
					} else {
						LOG.error("Could not find 'authors' array in JSON.");
					}
				} // publications

				LOG.info("Found {} publications.", publications.size());
			} else {
				LOG.error("Could not find 'docs' array in JSON.");
			}
		} catch (Exception e) {
			LOG.error(
					"Could not parse JSON string, its invalid. Exception: {}",
					e.getMessage());
		}
	}
}
