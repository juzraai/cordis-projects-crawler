package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectRcnListSeed(override var configuration: CordisCrawlerConfiguration? = null) : ICordisProjectRcnSeed {

	override fun projectRcns() = (configuration?.seed ?: "").run {
		if (matches(Regex("\\d+(,\\d+)*"))) split(",").map(String::toLong).asSequence()
		else null
	}
}