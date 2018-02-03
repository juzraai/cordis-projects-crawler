package com.github.juzraai.cordis.crawler.seed

import com.github.juzraai.cordis.crawler.*

/**
 * @author Zsolt Jurányi
 */
class DirectorySeed : IRcnSeedGenerator {

	override fun generateRcns(scope: String?, configuration: CordisCrawlerConfiguration): Sequence<Long>? {
		if ("dir".equals(scope, true)) {
			// TODO fetch RCNs from CORDIS cache dir
		}
		return null
	}
}