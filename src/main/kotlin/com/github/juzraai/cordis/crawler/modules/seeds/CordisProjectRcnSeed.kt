package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.model.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectRcnSeed : ICordisProjectRcnSeed {

	// TODO this is useless, RcnListSeed can handle single RCN too

	private var configuration: CordisCrawlerConfiguration? = null

	override fun initialize(configuration: CordisCrawlerConfiguration) {
		this.configuration = configuration
	}

	override fun projectRcns() = (configuration?.seed ?: "").run {
		if (matches(Regex("\\d+"))) sequenceOf(toLong())
		else null
	}
}