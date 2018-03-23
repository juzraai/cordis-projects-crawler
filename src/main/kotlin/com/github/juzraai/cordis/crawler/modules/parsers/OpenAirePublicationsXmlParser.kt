package com.github.juzraai.cordis.crawler.modules.parsers

import com.github.juzraai.cordis.crawler.model.openaire.sygma.*
import com.github.juzraai.cordis.crawler.util.*
import mu.*

/**
 * @author Zsolt Jurányi
 */
class OpenAirePublicationsXmlParser : IOpenAirePublicationsXmlParser {

	companion object : KLogging()

	override fun parsePublicationsXml(xml: String): List<Publication>? {
		return try {
			SimpleXmlParser.parseString(xml, Response::class.java).publications
		} catch (e: Exception) {
			logger.warn("Could not parse publications XML - ${e.message}")
			null
		}
	}
}