package com.github.juzraai.cordis.crawler.seed

import com.github.juzraai.cordis.crawler.*

/**
 * @author Zsolt Jurányi
 */
interface IRcnSeedGenerator {

	fun generateRcns(scope: String?, configuration: CordisCrawlerConfiguration): Sequence<Long>?
}