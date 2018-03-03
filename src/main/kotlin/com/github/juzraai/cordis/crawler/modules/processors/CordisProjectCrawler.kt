package com.github.juzraai.cordis.crawler.modules.processors

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.modules.*
import com.github.juzraai.cordis.crawler.modules.parsers.*
import com.github.juzraai.cordis.crawler.modules.readers.*
import com.github.juzraai.cordis.crawler.modules.readers.caches.*
import mu.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectCrawler(
		override var modules: CordisCrawlerModuleRegistry
) : ICordisProjectProcessor {

	companion object : KLogging()

	override fun process(cordisProject: CordisProject): CordisProject {
		return cordisProject.apply {
			logger.trace("Reading project XML: $rcn")
			val xml = readProjectXml(rcn)
			if (null != xml) {
				// TODO would be great not to rewrite every processed file...
				// TODO maybe remember which cache returned the file?
				logger.trace("Caching project XML: $rcn")
				cacheProjectXml(xml, rcn)
				logger.trace("Parsing project XML: $rcn")
				project = parseProjectXml(xml)
			}
		}
	}

	private fun readProjectXml(rcn: Long) =
			modules.ofType(ICordisProjectXmlReader::class.java).asSequence()
					.mapNotNull { it.projectXmlByRcn(rcn) }
					.firstOrNull()

	private fun cacheProjectXml(xml: String, rcn: Long) {
		modules.ofType(ICordisProjectXmlCache::class.java)
				.onEach { it.cacheProjectXml(xml, rcn) }
	}

	private fun parseProjectXml(xml: String) =
			modules.ofType(ICordisProjectXmlParser::class.java).asSequence()
					.mapNotNull { it.parseProjectXml(xml) }
					.firstOrNull()
}