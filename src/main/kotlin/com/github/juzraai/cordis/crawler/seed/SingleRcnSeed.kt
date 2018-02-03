package com.github.juzraai.cordis.crawler.seed

import com.github.juzraai.cordis.crawler.*

/**
 * @author Zsolt Jurányi
 */
class SingleRcnSeed : IRcnSeedGenerator {

	override fun generateRcns(scope: String?, configuration: CordisCrawlerConfiguration) =
			if (null != scope && scope.matches(Regex("\\d+"))) sequenceOf(scope.toLong())
			else null
}