package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.model.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectRcnRangeSeed(override var configuration: CordisCrawlerConfiguration? = null) : ICordisProjectRcnSeed {

	override fun projectRcns() = (configuration?.seed ?: "").run {
		if (matches(Regex("\\d+\\.\\.\\d+"))) {
			val bounds = split(Regex("\\.\\.")).map(String::toLong).sorted()
			LongRange(bounds[0], bounds[1]).asSequence()
		} else null
	}
}