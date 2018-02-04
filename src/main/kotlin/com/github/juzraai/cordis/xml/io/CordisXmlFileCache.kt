package com.github.juzraai.cordis.xml.io

import com.github.juzraai.cordis.crawler.*
import mu.*
import java.io.*
import java.util.*
import java.util.zip.*

/**
 * @author Zsolt Jurányi
 */
class CordisXmlFileCache : ICordisCrawlerConfigurationAware, ICordisXmlCache {

	override var configuration: CordisCrawlerConfiguration? = null

	companion object : KLogging()

	override fun readCordisXmlByRcn(rcn: Long): String? {
		val file = file(rcn)
		if (file.exists()) {
			logger.trace("Reading XML: $file")
			try {
				Scanner(GZIPInputStream(file.inputStream())).use {
					return it.useDelimiter("\\A").next()
				}
			} catch (e: Exception) {
				logger.warn("Failed to read XML: $file - ${e.message}")
			}
		}
		return null
	}

	override fun storeCordisXmlForRcn(rcn: Long, xml: String) {
		val file = file(rcn)
		logger.trace("Storing XML: $file")
		try {
			file.parentFile?.mkdirs()
			OutputStreamWriter(GZIPOutputStream(file.outputStream())).use { it.write(xml) }
		} catch (e: Exception) {
			logger.warn("Failed to store XML: $file - ${e.message}")
		}
	}

	private fun file(rcn: Long) = File(configuration!!.directory, "$rcn.xml.gz")
}