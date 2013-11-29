package hu.juranyi.zsolt.cordis.projects;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO JAVADOC
public class Export2Csv {

	private static final Logger LOG = LoggerFactory.getLogger(Export2Csv.class);

	public static void export(List<Project> projects, String filename) {
		LOG.info("Exporting {} projects to CSV...", projects.size());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Writer out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(filename), "UTF8");

			out.write("Name;");
			out.write("Title;");
			out.write("Website;");
			out.write("Coordinator's country;");
			out.write("Publications;");
			out.write("From;");
			out.write("To;");
			out.write("Status;");
			out.write("Contract type;");
			out.write("Cost;");
			out.write("Cost curr.;");
			out.write("EU contribution;");
			out.write("EU contrib. curr.;");
			out.write("Programme acronym;");
			out.write("Subprogramme area;");
			out.write("Record number (RCN);");
			out.write("Project reference;");
			out.write("Last updated on;");
			out.write("On CORDIS");
			out.write("\n");

			for (Project p : projects) {
				if (null == p)
					continue;

				StringBuilder sb = new StringBuilder();
				sb.append(p.getName());
				sb.append("#####");
				sb.append(p.getTitle());
				sb.append("#####");
				sb.append(p.getWebsite());
				sb.append("#####");

				try {
					sb.append(p.getCoordinator().getCountry());
				} catch (Exception e) {
					sb.append("N/A");
				}
				sb.append("#####");
				try {
					sb.append(p.getPublications().size());
				} catch (Exception e) {
					sb.append("N/A");
				}
				sb.append("#####");
				try {
					sb.append(dateFormat.format(p.getDatesFrom()));
				} catch (Exception e) {
					sb.append("N/A");
				}
				sb.append("#####");
				try {
					sb.append(dateFormat.format(p.getDatesTo()));
				} catch (Exception e) {
					sb.append("N/A");
				}
				sb.append("#####");

				sb.append(p.getStatus());
				sb.append("#####");

				sb.append(p.getContractType());
				sb.append("#####");
				sb.append(p.getCost());
				sb.append("#####");
				sb.append(p.getCostCurrency());
				sb.append("#####");
				sb.append(p.getEuContribution());
				sb.append("#####");
				sb.append(p.getEuContributionCurrency());
				sb.append("#####");

				sb.append(p.getProgrammeAcronym());
				sb.append("#####");
				sb.append(p.getSubprogrammeArea());
				sb.append("#####");

				sb.append(p.getRcn());
				sb.append("#####");
				sb.append(p.getReference());
				sb.append("#####");
				try {
					sb.append(dateFormat.format(p.getLastUpdatedOn()));
				} catch (Exception e) {
					sb.append("N/A");
				}
				sb.append("#####");

				sb.append(String.format(
						"http://cordis.europa.eu/projects/rcn/%d_en.html#####",
						p.getRcn()));

				sb.append("\n");
				String s = sb.toString();
				s = s.replaceAll(";", ",.").replaceAll("#####", ";");
				s = s.replaceAll(";\"", ";").replaceAll("\";", ";");
				s = s.replaceAll("null;", "N/A;");
				out.write(s);
			}
			out.flush();
		} catch (IOException e) {
			LOG.error("Failed to write CSV file: {}", e.getMessage());
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException e2) {
				}
			}
		}
	}
}
