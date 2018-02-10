package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.model.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectRcnDirectorySeed(override var configuration: CordisCrawlerConfiguration? = null) :
		ICordisProjectRcnSeed {

	override fun projectRcns() = if ("dir".equals(configuration?.seed, true)) {
		// TODO fetch RCNs from CORDIS cache dir - maybe use a cache feature like enumerate?
		throw UnsupportedOperationException()
	} else null
}