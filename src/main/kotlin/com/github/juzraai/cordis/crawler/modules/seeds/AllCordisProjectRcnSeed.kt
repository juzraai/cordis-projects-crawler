package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.model.*

/**
 * @author Zsolt Jurányi
 */
class AllCordisProjectRcnSeed(override var configuration: CordisCrawlerConfiguration? = null) : ICordisProjectRcnSeed {

	override fun projectRcns() = if ("all".equals(configuration?.seed, true)) {
		// TODO fetch all project RCNs from CORDIS
		throw UnsupportedOperationException()
	} else null
}