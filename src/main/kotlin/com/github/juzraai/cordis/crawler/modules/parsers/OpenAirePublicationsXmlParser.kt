package com.github.juzraai.cordis.crawler.modules.parsers

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.model.openaire.sygma.*
import mu.*
import org.simpleframework.xml.convert.*
import org.simpleframework.xml.core.*
import java.util.*

/**
 * @author Zsolt Jurányi
 */
class OpenAirePublicationsXmlParser(override var configuration: CordisCrawlerConfiguration? = null)
	: IOpenAirePublicationsXmlParser {

	companion object : KLogging()

	private var persister = Persister(RegistryStrategy(Registry().apply {
		bind(Date::class.java, DateConverter::class.java)
	}))

	override fun parsePublicationsXml(xml: String): List<Publication>? {
		try {
			xml.byteInputStream().use {
				return persister.read(Response::class.java, it, false).publications
			}
		} catch (e: Exception) {
			logger.warn("Could not parse publications XML - ${e.message}")
			return null
		}
	}
}