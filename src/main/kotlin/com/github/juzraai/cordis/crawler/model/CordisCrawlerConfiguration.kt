package com.github.juzraai.cordis.crawler.model

import com.beust.jcommander.*
import java.util.*

/**
 * @author Zsolt Jurányi
 */
data class CordisCrawlerConfiguration(
		// TODO builder
		// TODO make it open (kotlin-allopen?) + write extending example to doc

		val timestamp: Date = Date(),

		@Parameter(names = ["-e", "--crawl-everything"], description = "Crawl not just project metadata from CORDIS but all available related information too.")
		var crawlEverything: Boolean = false,

		@Parameter(names = ["-p", "--crawl-publications"], description = "Crawl publication metadata too for each project using OpenAIRE API.")
		var crawlPublications: Boolean = false,

		@Parameter(names = ["-f", "--force-download"], description = "Forces downloading project data even if it already exists in output directory.")
		var forceDownload: Boolean = false,

		@Parameter(names = ["-m", "--mysql-export"], description = "Turns on MySQL export of project data. Pass connection parameters in this format: 'user@host:port/schema'. Specify connection password using the '-P' option. Schema must be created first.")
		var mysqlExport: String? = null,

		@Parameter(names = ["-o", "--output-dir"], description = "Path of directory where downloaded/exported files will be placed.")
		var outputDirectory: String = "cordis-data",

		@Parameter(names = ["-P"], description = "Password for MySQL connection.", password = true)
		var password: String? = null,

		@Parameter(names = ["-q", "--quiet"], description = "Turns off all console output. Has no effect when verbose mode is activated.")
		var quiet: Boolean = false,

		@Parameter(names = ["-s", "--seed"], description = "Which projects to download. You can pass a single RCN, RCN list separated by ',', RCN range in 'x..y' format, project URL, CORDIS search result URL, 'all' to crawl all projects or 'dir' to reprocess RCNs in output directory.")
		var seed: String? = null,

		@Parameter(names = ["-t", "--tsv-export"], description = "Turns on TSV export of project data. Output files will be in output directory under 'exports'.")
		var tsvExport: Boolean = false,

		@Parameter(names = ["-v", "--verbose"], description = "Turns on debug/trace log messages. Quiet mode will have no effect.")
		var verbose: Boolean = false
)