package com.github.juzraai.cordis.xml.parser

import com.github.juzraai.cordis.xml.model.*
import mu.*
import org.simpleframework.xml.convert.*
import org.simpleframework.xml.core.*
import java.util.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectXmlParser : ICordisXmlParser {

	companion object : KLogging()

	var persister = Persister(RegistryStrategy(Registry().apply {
		bind(Date::class.java, DateConverter::class.java)
	}))

	override fun parseCordisXml(xml: String): CordisXml? {
		try {
			xml.byteInputStream().use {
				val project = persister.read(Project::class.java, it, false)
				return if (null == project) null else CordisXml(project = project)
			}
		} catch (e: Exception) {
			logger.warn("Could not parse XML - ${e.message}")
			return null
		}
	}
}