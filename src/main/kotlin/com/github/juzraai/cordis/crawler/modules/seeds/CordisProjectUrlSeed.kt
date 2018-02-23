package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.model.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectUrlSeed : ICordisProjectRcnSeed {

	private var configuration: CordisCrawlerConfiguration? = null

	override fun initialize(configuration: CordisCrawlerConfiguration) {
		this.configuration = configuration
	}

	override fun projectRcns() = (configuration?.seed ?: "").run {
		if (matches(Regex("http://cordis\\.europa\\.eu/project/rcn/\\d+_\\w+.(html|xml)"))) {
			listOf(replace(Regex("\\D+"), "").toLong()).iterator()
		} else null
	}
}