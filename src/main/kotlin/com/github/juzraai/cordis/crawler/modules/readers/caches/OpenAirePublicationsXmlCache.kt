package com.github.juzraai.cordis.crawler.modules.readers.caches

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.model.cordis.*
import mu.*
import java.io.*
import java.util.*
import java.util.zip.*

/**
 * @author Zsolt Jurányi
 */
class OpenAirePublicationsXmlCache : IOpenAirePublicationsXmlCache {

	// TODO ? merge 2 caches, then we can reduce code redundancy

	companion object : KLogging()

	private var configuration: CordisCrawlerConfiguration? = null

	override fun initialize(configuration: CordisCrawlerConfiguration) {
		this.configuration = configuration
	}

	override fun publicationsXmlByProject(project: Project): String? {
		val file = publicationsXmlTargetFile(project.rcn!!)
		if (file.exists()) {
			logger.trace("Reading publications XML: $file")
			try {
				Scanner(GZIPInputStream(file.inputStream())).use {
					return it.useDelimiter("\\A").next()
				}
			} catch (e: Exception) {
				logger.warn("Failed to read publications XML: $file - ${e.message}")
			}
		}
		return null
	}

	override fun cachePublicationsXml(rcn: Long, xml: String) {
		val file = publicationsXmlTargetFile(rcn)
		logger.trace("Storing publications XML: $file")
		try {
			file.parentFile?.mkdirs()
			OutputStreamWriter(GZIPOutputStream(file.outputStream())).use { it.write(xml) }
		} catch (e: Exception) {
			logger.warn("Failed to store publications XML: $file - ${e.message}")
		}
	}

	private fun publicationsXmlTargetFile(rcn: Long) = File(configuration!!.directory, "publications${File.separator}$rcn.xml.gz")
}