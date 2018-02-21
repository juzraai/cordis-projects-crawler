package com.github.juzraai.cordis.crawler.modules.readers.caches

import com.github.juzraai.cordis.crawler.model.*
import mu.*
import java.io.*
import java.util.*
import java.util.zip.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectXmlFileCache : ICordisProjectXmlCache {

	companion object : KLogging()

	private var configuration: CordisCrawlerConfiguration? = null

	override fun initialize(configuration: CordisCrawlerConfiguration) {
		this.configuration = configuration
	}

	override fun projectXmlByRcn(rcn: Long): String? {
		val file = projectXmlTargetFile(rcn)
		if (file.exists()) {
			logger.trace("Reading project XML: $file")
			try {
				Scanner(GZIPInputStream(file.inputStream())).use {
					return it.useDelimiter("\\A").next()
				}
			} catch (e: Exception) {
				logger.warn("Failed to read project XML: $file - ${e.message}")
			}
		}
		return null
	}

	override fun cacheProjectXml(rcn: Long, xml: String) {
		val file = projectXmlTargetFile(rcn)
		logger.trace("Storing project XML: $file")
		try {
			file.parentFile?.mkdirs()
			OutputStreamWriter(GZIPOutputStream(file.outputStream())).use { it.write(xml) }
		} catch (e: Exception) {
			logger.warn("Failed to store project XML: $file - ${e.message}")
		}
	}

	private fun projectXmlTargetFile(rcn: Long) = File(configuration!!.directory, "project${File.separator}$rcn.xml.gz")
}