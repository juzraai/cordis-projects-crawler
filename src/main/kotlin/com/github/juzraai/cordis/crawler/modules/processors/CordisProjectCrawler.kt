package com.github.juzraai.cordis.crawler.modules.processors

import com.github.juzraai.cordis.crawler.*
import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.modules.*
import com.github.juzraai.cordis.crawler.modules.readers.caches.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectCrawler(
		override var modules: CordisCrawlerModuleRegistry,
		override var configuration: CordisCrawlerConfiguration? = null
) : ICordisProjectProcessor {

	override fun process(cordisProject: CordisProject): CordisProject {
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

	private fun readProjectXml(rcn: Long) = modules.projectXmlReaders.asSequence()
			.mapNotNull { it.projectXmlByRcn(rcn) }
			.firstOrNull()

	private fun cacheProjectXml(rcn: Long, xml: String) {
		modules.projectXmlReaders
				.mapNotNull { it as? ICordisProjectXmlCache }
				.onEach { it.cacheProjectXml(rcn, xml) }
	}

	private fun parseProjectXml(xml: String) = modules.projectXmlParsers.asSequence()
			.mapNotNull { it.parseProjectXml(xml) }
			.firstOrNull()
}