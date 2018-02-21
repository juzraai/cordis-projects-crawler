package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.model.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectRcnRangeSeed : ICordisProjectRcnSeed {

	private var configuration: CordisCrawlerConfiguration? = null

	override fun initialize(configuration: CordisCrawlerConfiguration) {
		this.configuration = configuration
	}

	override fun projectRcns() = (configuration?.seed ?: "").run {
		if (matches(Regex("\\d+\\.\\.\\d+"))) {
			val bounds = split(Regex("\\.\\.")).map(String::toLong).sorted()
			LongRange(bounds[0], bounds[1]).asSequence()
		} else null
	}
}