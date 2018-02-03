package com.github.juzraai.cordis.crawler.seed

import com.github.juzraai.cordis.crawler.*

/**
 * @author Zsolt Jurányi
 */
class AllRcnsSeed : IRcnSeedGenerator {

	// TODO later: able to fetch highest result RCN

	override fun generateRcns(scope: String?, configuration: CordisCrawlerConfiguration): Sequence<Long>? {
		if ("all".equals(scope, true)) {
			// TODO fetch highest project RCN from CORDIS
		}
		return null
	}
}