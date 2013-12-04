package hu.juranyi.zsolt.cordis.projects;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * CORDIS Projects Data Crawler application. This class defines the main method
 * which provides a CLI for the crawler methods.
 * 
 * @author Zsolt Jurányi
 * 
 */
public class CordisProjectsApp {

	private static final String VERSION = "1.3.0-SNAPSHOT";

	/**
	 * Provides a CLI which helps users set up and run crawls to fetch
	 * information from CORDIS projects.
	 * 
	 * @param args
	 *            Command line arguments. Run method with empty argument list to
	 *            see usage information.
	 */
	public static void main(String[] args) {
		// header :-)
		System.out
				.println("## -----------------------------------------------------");
		System.out.println("## CORDIS Projects Data Crawler - version "
				+ VERSION);
		System.out.println("## by Zsolt Juranyi");
		System.out
				.println("## https://github.com/juzraai/Cordis-Projects-Crawler");
		System.out
				.println("## -----------------------------------------------------");

		Options options = new Options();

		// define commands
		OptionGroup group = new OptionGroup();
		group.addOption(new Option("a", "download-all", false,
				"Download all projects' data."));
		group.addOption(new Option("1", "download-by-rcn", true,
				"Download project data by the given RCN."));
		group.setRequired(true);
		options.addOptionGroup(group);

		// define options
		options.addOption("d", "output-dir", true,
				"Directory where project data files should be put/found.");
		options.addOption("df", "project-filename", true,
				"Filename template of project data HTML page. "
						+ "Must include a '%d' placeholder for RCN. "
						+ "Ex.: project-%d.html");
		options.addOption("lf", "publist-filename", true,
				"Filename template of publication list JSON file. "
						+ "Must include a '%d' placeholder for RCN. "
						+ "Ex.: project-%d.json");
		options.addOption("ns", "no-skip", false,
				"Program will NOT skip already existing files, will "
						+ "re-download them instead.");
		options.addOption("rd", "read-rcns-from-dir", false,
				"When using beside 'a', the program will read RCNs from "
						+ "already downloaded project pages' filenames (using "
						+ "filename template), instead of crawling CORDIS. "
						+ "This is useful when you've got only project pages "
						+ "and need publication list JSON files.");
		options.addOption("xcsv", "export-2-csv", true,
				"Projects' data will be exported to a CSV file. Publication "
						+ "list and participants' data will not be included.");
		options.addOption(OptionBuilder
				.hasArgs(4)
				.withLongOpt("export-2-mysql")
				.withDescription(
						"All data will be exported into a MySQL database. "
								+ "Arguments: <host:port> <database name> "
								+ "<user> <password>").create("xdb"));

		options.getOption("1").setType(Number.class);

		// parse
		CommandLineParser parser = new BasicParser();
		try {
			CommandLine line = parser.parse(options, args);
			ProjectDownloader downloader = new ProjectDownloader();

			// pre-check MySQL connection if needed
			// TODO not an elegant solution for pre-check :)
			if (line.hasOption("xdb")) {
				String[] v = line.getOptionValues("xdb");
				if (v != null && 4 == v.length) {
					String host = v[0];
					String name = v[1];
					String user = v[2];
					String pass = v[3];
					Export2MySQL x = new Export2MySQL(host, name, user, pass);
					x.export(new ArrayList<Project>());
				} else {
					System.out.println("Not enough parameters for 'xdb' !");
				}
			}

			// options
			if (line.hasOption("d")) {
				downloader.outputDir(line.getOptionValue("d"));
			}
			if (line.hasOption("df")) {
				downloader.projectFilename(line.getOptionValue("df"));
			}
			if (line.hasOption("lf")) {
				downloader.publistFilename(line.getOptionValue("lf"));
			}
			if (line.hasOption("ns")) {
				downloader.skipExisting(false);
			}
			if (line.hasOption("rd")) {
				downloader.readRCNsFromDirectory(true);
			}

			// downloader commands
			List<Project> projects = new ArrayList<Project>();
			if (line.hasOption("1")) {
				int rcn = ((Number) line.getParsedOptionValue("1")).intValue();
				Project project = downloader.byRCN(rcn);
				projects = new ArrayList<Project>();
				projects.add(project);
			} else if (line.hasOption("a")) {
				projects.addAll(downloader.all());
			}

			// export commands
			if (line.hasOption("xcsv")) {
				Export2Csv.export(projects, line.getOptionValue("xcsv"));
			}
			if (line.hasOption("xdb")) {
				String[] v = line.getOptionValues("xdb");
				if (v != null && 4 == v.length) {
					String host = v[0];
					String name = v[1];
					String user = v[2];
					String pass = v[3];
					Export2MySQL x = new Export2MySQL(host, name, user, pass);
					x.export(projects);
				} else {
					System.out.println("Not enough parameters for 'xdb' !");
				}
			}
		} catch (ParseException exp) {
			System.err.println(exp.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("cordis-projects", options, true);
		}
	}
}
