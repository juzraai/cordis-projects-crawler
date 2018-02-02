package com.github.juzraai.cordis.projects.read

import com.github.juzraai.cordis.projects.cli.*
import mu.*
import java.io.*
import java.util.*
import java.util.zip.*

/**
 * @author Zsolt Jurányi
 */
class CordisXmlFileCache : ICordisXmlCache {

	companion object : KLogging()

	override fun readCordisXmlByRcn(rcn: Long, configuration: CpcConfiguration): String? {
		val file = file(rcn, configuration)
		logger.trace("Reading XML: $file")
		try {
			Scanner(GZIPInputStream(file.inputStream())).use {
				return it.useDelimiter("\\A").next()
			}
		} catch (e: Exception) {
			logger.warn("Failed to read XML: $file - ${e.message}")
			return null
		}
	}

	override fun storeCordisXmlForRcn(rcn: Long, xml: String, configuration: CpcConfiguration) {
		val file = file(rcn, configuration)
		logger.trace("Storing XML: $file")
		try {
			file.parentFile?.mkdirs()
			OutputStreamWriter(GZIPOutputStream(file.outputStream())).use { it.write(xml) }
		} catch (e: Exception) {
			logger.warn("Failed to store XML: $file - ${e.message}")
		}
	}

	private fun file(rcn: Long, configuration: CpcConfiguration) = File(configuration.directory, "$rcn.xml.gz")
}