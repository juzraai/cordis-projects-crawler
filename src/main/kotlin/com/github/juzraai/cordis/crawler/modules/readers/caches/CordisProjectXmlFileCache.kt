package com.github.juzraai.cordis.crawler.modules.readers.caches

import com.github.juzraai.cordis.crawler.model.*
import mu.*
import java.io.*
import java.util.*
import java.util.zip.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectXmlFileCache(override var configuration: CordisCrawlerConfiguration? = null) : ICordisProjectXmlCache {

	companion object : KLogging()

	// TODO should store XMLs in subdirectories like: /contentType/rcn_en.xml - so we need URLs here too?

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