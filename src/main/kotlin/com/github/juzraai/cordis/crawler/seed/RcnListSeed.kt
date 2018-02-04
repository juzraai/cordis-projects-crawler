package com.github.juzraai.cordis.crawler.seed

import com.github.juzraai.cordis.crawler.*

/**
 * @author Zsolt Jurányi
 */
class RcnListSeed : IRcnSeedGenerator {

	override fun generateRcns(scope: String?, configuration: CordisCrawlerConfiguration): Sequence<Long>? {
		return if (scope != null && scope.matches(Regex("\\d+(,\\d+)*")))
			scope.split(",").map(String::toLong).asSequence()
		else null
	}
}