package com.github.juzraai.cordis.crawler.seed

import com.github.juzraai.cordis.crawler.*

/**
 * @author Zsolt Jurányi
 */
class SearchUrlSeed : IRcnSeedGenerator {

	override fun generateRcns(scope: String?, configuration: CordisCrawlerConfiguration): Sequence<Long>? {
		// https://cordis.europa.eu/projects/result_en?
		// https://cordis.europa.eu/search/result_en?
		// TODO match URL prefixes above, crawl project RCNs here
		return null
	}
}