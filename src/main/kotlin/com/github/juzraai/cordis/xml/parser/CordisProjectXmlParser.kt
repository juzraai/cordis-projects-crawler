package com.github.juzraai.cordis.xml.parser

import com.github.juzraai.cordis.xml.model.*
import mu.*
import org.simpleframework.xml.convert.*
import org.simpleframework.xml.core.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectXmlParser : ICordisXmlParser {

	companion object : KLogging()

	private var persister: Persister = Persister(AnnotationStrategy())

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