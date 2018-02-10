package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.model.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectRcnSeed(override var configuration: CordisCrawlerConfiguration? = null) : ICordisProjectRcnSeed {

	override fun projectRcns() = (configuration?.seed ?: "").run {
		if (matches(Regex("\\d+"))) sequenceOf(toLong())
		else null
	}
}