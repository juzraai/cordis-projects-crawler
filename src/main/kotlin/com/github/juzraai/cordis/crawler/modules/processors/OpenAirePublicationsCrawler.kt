package com.github.juzraai.cordis.crawler.modules.processors

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.model.cordis.*
import com.github.juzraai.cordis.crawler.modules.*
import com.github.juzraai.cordis.crawler.modules.parsers.*
import com.github.juzraai.cordis.crawler.modules.readers.*
import com.github.juzraai.cordis.crawler.modules.readers.caches.*
import mu.*

/**
 * @author Zsolt Jurányi
 */
class OpenAirePublicationsCrawler : ICordisCrawlerRecordProcessor {
	/*
		OpenAIRE API: http://api.openaire.eu/search/publications

		A)
			XML: http://api.openaire.eu/search/publications?projectID=REFERENCE
			XSD: https://www.openaire.eu/schema/1.0/oaf-1.0.xsd
		B)
			JSON: http://api.openaire.eu/search/publications?projectID=REFERENCE&format=json
		C)
			Sygma XML: http://api.openaire.eu/search/publications?projectID=REFERENCE&model=sygma

		- Sygma is a simplified model, let's use this one
		- max record count is 10K, use it, then no paging is needed
	 */

	companion object : KLogging()

	private var configuration: CordisCrawlerConfiguration? = null
	private var modules: CordisCrawlerModuleRegistry? = null

	override fun initialize(configuration: CordisCrawlerConfiguration, modules: CordisCrawlerModuleRegistry) {
		this.configuration = configuration
		this.modules = modules
	}

	override fun process(cordisCrawlerRecord: CordisCrawlerRecord): CordisCrawlerRecord? {
		return cordisCrawlerRecord.apply {
			if (configuration!!.crawlEverything || configuration!!.crawlPublications) {
				val p = project
				if (null != p) {
					logger.trace("Reading publications XML: $rcn")
					val xml = readPublicationsXml(p)
					if (null != xml) {
						logger.trace("Caching publications XML: $rcn")
						cachePublicationsXml(xml, p)
						logger.trace("Parsing publications XML: $rcn")
						publications = parsePublicationsXml(xml)
					}
				}
			}
		}
	}

	private fun readPublicationsXml(project: Project) =
			modules!!.ofType(IOpenAirePublicationsXmlReader::class.java).asSequence()
					.mapNotNull { it.publicationsXmlByProject(project) }
					.firstOrNull()

	private fun cachePublicationsXml(xml: String, project: Project) {
		modules!!.ofType(IOpenAirePublicationsXmlCache::class.java)
				.onEach { it.cachePublicationsXml(xml, project) }
	}

	private fun parsePublicationsXml(xml: String) =
			modules!!.ofType(IOpenAirePublicationsXmlParser::class.java).asSequence()
					.mapNotNull { it.parsePublicationsXml(xml) }
					.firstOrNull()
}