package com.github.juzraai.cordis.crawler.seed

import com.github.juzraai.cordis.crawler.*

/**
 * @author Zsolt Jurányi
 */
class AllRcnsSeed : IRcnSeedGenerator {

	override fun generateRcns(scope: String?, configuration: CordisCrawlerConfiguration): Sequence<Long>? {
		// TODO instead of "all", it should match to "https?://cordis\.europa\.eu/"
		if ("all".equals(scope, true)) {
			// TODO fetch all project RCNs from a CORDIS query URL given by user
		}
		return null
	}
}