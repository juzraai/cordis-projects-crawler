package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.model.*
import java.io.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectRcnDirectorySeed : ICordisProjectRcnSeed {

	private var configuration: CordisCrawlerConfiguration? = null

	override fun initialize(configuration: CordisCrawlerConfiguration) {
		this.configuration = configuration
	}

	override fun projectRcns() = if ("dir".equals(configuration?.seed, true)) {
		// TODO using an "enumeration" feature of all caches would be more elegant
		File(configuration?.outputDirectory, "project")
				.listFiles(FileFilter { it.isDirectory && it.name.matches(Regex("\\d+")) })
				.map { it.name.replace(Regex("\\D"), "").toLong() }
				.iterator()
	} else null
}