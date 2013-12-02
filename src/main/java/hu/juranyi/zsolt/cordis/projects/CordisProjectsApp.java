package hu.juranyi.zsolt.cordis.projects;

import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
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

	private static final String VERSION = "1.2.1-SNAPSHOT";

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
				"When using beside 'a', the program will export processed "
						+ "projects to a CSV file. Publication list will not "
						+ "be included.");

		options.getOption("1").setType(Number.class);

		// parse
		CommandLineParser parser = new BasicParser();
		try {
			CommandLine line = parser.parse(options, args);
			ProjectDownloader downloader = new ProjectDownloader();

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

			// commands
			if (line.hasOption("1")) {
				downloader.byRCN(((Number) line.getParsedOptionValue("1"))
						.intValue());
			} else if (line.hasOption("a")) {
				List<Project> projects = downloader.all();
				if (line.hasOption("xcsv")) {
					Export2Csv.export(projects, line.getOptionValue("xcsv"));
				}
			}
		} catch (ParseException exp) {
			System.err.println(exp.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("cordis-projects", options, true);
		}
	}
}
