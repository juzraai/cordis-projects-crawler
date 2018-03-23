package com.github.juzraai.cordis.crawler.modules.parsers

import com.github.juzraai.cordis.crawler.model.cordis.*
import com.github.juzraai.cordis.crawler.util.*
import mu.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectXmlParser : ICordisProjectXmlParser {

	companion object : KLogging()

	override fun parseProjectXml(xml: String): Project? {
		return try {
			SimpleXmlParser.parseString(xml, Project::class.java)
		} catch (e: Exception) {
			logger.warn("Could not parse project XML - ${e.message}")
			null
		}
	}
}