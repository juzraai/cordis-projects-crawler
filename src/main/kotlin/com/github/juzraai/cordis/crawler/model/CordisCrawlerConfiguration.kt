package com.github.juzraai.cordis.crawler.model

import com.beust.jcommander.*
import java.util.*

/**
 * @author Zsolt Jurányi
 */
data class CordisCrawlerConfiguration(
		// TODO builder
		// TODO make it open (kotlin-allopen?)

		val timestamp: Date = Date(),

		@Parameter(names = ["-d", "--directory"], description = "Where put downloaded/exported files")
		var directory: String = "cordis-data",

		@Parameter(names = ["-s", "--seed"], description = "Which projects to download. You can pass a single RCN, " +
				"RCN list separated by ',', RCN range in 'x..y' format, project URL.")
		var seed: String? = null,

		@Parameter(names = ["-xt", "--tsv"], description = "Turns on TSV export of project data. Output file will be " +
				"in output directory under 'exports'.")
		var tsv: Boolean = false,

		@Parameter(names = ["-v", "--verbose"], description = "Turns on debug/trace log messages")
		var verbose: Boolean = false
)