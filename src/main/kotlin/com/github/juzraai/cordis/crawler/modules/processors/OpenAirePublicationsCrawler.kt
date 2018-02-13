package com.github.juzraai.cordis.crawler.modules.processors

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.model.cordis.*
import com.github.juzraai.cordis.crawler.modules.*
import com.github.juzraai.cordis.crawler.modules.readers.caches.*
import mu.*

/**
 * @author Zsolt Jurányi
 */
class OpenAirePublicationsCrawler(
		override var modules: CordisCrawlerModuleRegistry,
		override var configuration: CordisCrawlerConfiguration? = null
) : ICordisProjectProcessor {
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

	override fun process(cordisProject: CordisProject): CordisProject? {
		return cordisProject.apply {
			val p = project
			if (null != p) {
				logger.trace("Reading publications XML: $rcn")
				val xml = readPublicationsXml(p)
				if (null != xml) {
					logger.trace("Caching publications XML: $rcn")
					cachePublicationsXml(rcn, xml)
					logger.trace("Parsing publications XML: $rcn")
					publications = parsePublicationsXml(xml)
				}
			}
		}
	}

	private fun readPublicationsXml(project: Project) = modules.publicationsXmlReaders.asSequence()
			.mapNotNull { it.publicationsXmlByProject(project) }
			.firstOrNull()

	private fun cachePublicationsXml(rcn: Long, xml: String) {
		modules.publicationsXmlReaders
				.mapNotNull { it as? IOpenAirePublicationsXmlCache }
				.onEach { it.cachePublicationsXml(rcn, xml) }
	}

	private fun parsePublicationsXml(xml: String) = modules.publicationsXmlParsers.asSequence()
			.mapNotNull { it.parsePublicationsXml(xml) }
			.firstOrNull()
}