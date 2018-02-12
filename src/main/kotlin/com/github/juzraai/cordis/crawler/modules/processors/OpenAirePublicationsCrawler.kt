package com.github.juzraai.cordis.crawler.modules.processors

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.model.openaire.sygma.*
import com.github.juzraai.cordis.crawler.modules.*
import com.github.juzraai.cordis.crawler.modules.parsers.*
import org.jsoup.*
import org.simpleframework.xml.convert.*
import org.simpleframework.xml.core.*
import java.util.*

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

	var persister = Persister(RegistryStrategy(Registry().apply {
		bind(Date::class.java, DateConverter::class.java)
	}))

	override fun process(cordisProject: CordisProject): CordisProject? {
		// TODO move out to modules
		val xml = Jsoup.connect("http://api.openaire.eu/search/publications?projectID=${cordisProject.project!!.reference}&model=sygma&size=10000").maxBodySize(10_000_000).timeout(60_000).execute().body()
		// TODO cache
		try {
			xml.byteInputStream().use {
				val r = persister.read(Response::class.java, it, false)
				cordisProject.publications = r.publications
			}
			return cordisProject
		} catch (e: Exception) {
			CordisProjectXmlParser.logger.warn("Could not parse XML - ${e.message}")
			return null
		}
	}
}