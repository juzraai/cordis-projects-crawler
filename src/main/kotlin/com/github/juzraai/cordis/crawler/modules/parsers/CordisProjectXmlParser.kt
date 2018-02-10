package com.github.juzraai.cordis.crawler.modules.parsers

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.model.xml.*
import mu.*
import org.simpleframework.xml.convert.*
import org.simpleframework.xml.core.*
import java.util.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectXmlParser(override var configuration: CordisCrawlerConfiguration? = null) : ICordisProjectXmlParser {

	companion object : KLogging()

	var persister = Persister(RegistryStrategy(Registry().apply {
		bind(Date::class.java, DateConverter::class.java)
	}))

	override fun parseProjectXml(xml: String): Project? {
		try {
			xml.byteInputStream().use {
				return persister.read(Project::class.java, it, false)
			}
		} catch (e: Exception) {
			logger.warn("Could not parse XML - ${e.message}")
			return null
		}
	}
}