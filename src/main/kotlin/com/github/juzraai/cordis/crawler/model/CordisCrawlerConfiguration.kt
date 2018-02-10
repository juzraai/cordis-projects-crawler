package com.github.juzraai.cordis.crawler.model

import com.beust.jcommander.*

/**
 * @author Zsolt Jurányi
 */
data class CordisCrawlerConfiguration(
		// TODO builder
		// TODO make it open (kotlin-allopen?)

		@Parameter(names = ["-d", "--directory"])
		var directory: String = "cordis-data",

		@Parameter(names = ["-s", "--seed"])
		var seed: String? = null,

		@Parameter(names = ["-v", "--verbose"])
		var verbose: Boolean = false
)