package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.model.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectSearchUrlSeed : ICordisProjectRcnSeed {

	private var configuration: CordisCrawlerConfiguration? = null

	override fun initialize(configuration: CordisCrawlerConfiguration) {
		this.configuration = configuration
	}

	override fun projectRcns() = (configuration?.seed ?: "").run {
		if (matches(Regex("https://cordis\\.europa\\.eu/(projects|search)/result_en\\?.*"))) {
			// TODO match URL prefixes above, crawl only project RCNs here!
			throw UnsupportedOperationException()
		} else null
	}
}