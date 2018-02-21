package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.model.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectRcnListSeed : ICordisProjectRcnSeed {

	private var configuration: CordisCrawlerConfiguration? = null

	override fun initialize(configuration: CordisCrawlerConfiguration) {
		this.configuration = configuration
	}

	override fun projectRcns() = (configuration?.seed ?: "").run {
		if (matches(Regex("\\d+(,\\d+)*"))) split(",").map(String::toLong).asSequence()
		else null
	}
}