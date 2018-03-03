package com.github.juzraai.cordis.crawler.modules.readers.caches

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.model.cordis.*
import com.github.juzraai.cordis.crawler.util.*
import java.io.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectFileCache : ICordisProjectXmlCache, IOpenAirePublicationsXmlCache {

	private var configuration: CordisCrawlerConfiguration? = null

	override fun initialize(configuration: CordisCrawlerConfiguration) {
		this.configuration = configuration
	}

	override fun cacheProjectXml(xml: String, rcn: Long) {
		GzippedTextFile(projectXmlFile(rcn)).write(xml)
	}

	override fun cachePublicationsXml(xml: String, project: Project) {
		GzippedTextFile(publicationsXmlFile(project)).write(xml)
	}

	override fun projectXmlByRcn(rcn: Long) =
			GzippedTextFile(projectXmlFile(rcn)).read()

	override fun publicationsXmlByProject(project: Project) =
			GzippedTextFile(publicationsXmlFile(project)).read()

	private fun projectDirectory(rcn: String) = File(configuration!!.directory, "project/$rcn")

	private fun projectXmlFile(rcn: Long): File {
		val s = rcnAsString(rcn)
		return File(projectDirectory(s), "$s-project.xml.gz")
	}

	private fun publicationsXmlFile(project: Project): File {
		val s = rcnAsString(project.rcn!!)
		return File(projectDirectory(s), "$s-publications.xml.gz")
	}

	private fun rcnAsString(rcn: Long) = String.format("%6d", rcn).replace(' ', '0')
}