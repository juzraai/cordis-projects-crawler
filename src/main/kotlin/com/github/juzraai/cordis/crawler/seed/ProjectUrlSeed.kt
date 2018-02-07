package com.github.juzraai.cordis.crawler.seed

import com.github.juzraai.cordis.crawler.*

/**
 * @author Zsolt Jurányi
 */
class ProjectUrlSeed : IRcnSeedGenerator {

	override fun generateRcns(scope: String?, configuration: CordisCrawlerConfiguration): Sequence<Long>? =
			if (null != scope && scope.matches(Regex("http://cordis\\.europa\\.eu/project/rcn/\\d+_\\w+.html"))) {
				sequenceOf(scope.replace(Regex("\\D+"), "").toLong())
			} else null
}