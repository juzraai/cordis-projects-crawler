package com.github.juzraai.cordis.crawler

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.modules.*
import com.github.juzraai.cordis.crawler.modules.readers.caches.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectCrawler(
		var configuration: CordisCrawlerConfiguration = CordisCrawlerConfiguration(),
		var modules: CordisCrawlerModuleRegistry = CordisCrawlerModuleRegistry()
) {
	fun crawlProject(rcn: Long): CordisProject {
		return crawlProject(CordisProject(rcn))
	}

	fun crawlProject(cordisProject: CordisProject): CordisProject {
		println(cordisProject)
		return cordisProject.apply {
			CordisCrawler.logger.trace("Reading project XML: $rcn")
			val xml = readProjectXml(rcn)
			if (null != xml) {
				CordisCrawler.logger.trace("Caching project XML: $rcn")
				cacheProjectXml(rcn, xml)
				CordisCrawler.logger.trace("Parsing project XML: $rcn")
				project = parseProjectXml(xml)
			}
		}
	}

	private fun readProjectXml(rcn: Long) = modules.readers.asSequence()
			.mapNotNull { it.projectXmlByRcn(rcn) }
			.firstOrNull()

	private fun cacheProjectXml(rcn: Long, xml: String) {
		modules.readers
				.mapNotNull { it as? ICordisProjectXmlCache }
				.onEach { it.cacheProjectXml(rcn, xml) }
	}

	private fun parseProjectXml(xml: String) = modules.parsers.asSequence()
			.mapNotNull { it.parseProjectXml(xml) }
			.firstOrNull()
}