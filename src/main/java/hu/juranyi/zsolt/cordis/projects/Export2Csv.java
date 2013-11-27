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
				out.write(p.getName() + ";");
				out.write(p.getTitle() + ";");
				out.write(p.getWebsite() + ";");

				out.write(p.getCoordinator().getCountry() + ";");

				out.write(p.getPublications().size() + ";");

				out.write(dateFormat.format(p.getDatesFrom()) + ";");
				out.write(dateFormat.format(p.getDatesTo()) + ";");
				out.write(p.getStatus() + ";");

				out.write(p.getContractType() + ";");
				out.write(p.getCost() + ";");
				out.write(p.getCostCurrency() + ";");
				out.write(p.getEuContribution() + ";");
				out.write(p.getEuContributionCurrency() + ";");

				out.write(p.getProgrammeAcronym() + ";");
				out.write(p.getSubprogrammeArea() + ";");

				out.write(p.getRcn() + ";");
				out.write(p.getReference() + ";");
				out.write(dateFormat.format(p.getLastUpdatedOn()) + ";");

				out.write(String.format(
						"http://cordis.europa.eu/projects/rcn/%d_en.html;",
						p.getRcn()));

				out.write("\n");
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
