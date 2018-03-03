package com.github.juzraai.cordis.crawler.util

import mu.*
import java.io.*
import java.util.*
import java.util.zip.*

/**
 * @author Zsolt Jurányi
 */
class GzippedTextFile(val file: File) {

	companion object : KLogging()

	fun read(): String? {
		if (file.exists()) {
			logger.trace("Reading file: $file")
			try {
				Scanner(GZIPInputStream(file.inputStream())).use {
					return it.useDelimiter("\\A").next()
				}
			} catch (e: Exception) {
				logger.warn("Failed to read file: $file - ${e.message}")
			}
		}
		return null
	}

	fun write(content: String) {
		logger.trace("Writing file: $file")
		try {
			file.parentFile?.mkdirs()
			OutputStreamWriter(GZIPOutputStream(file.outputStream())).use { it.write(content) }
		} catch (e: Exception) {
			logger.warn("Failed to write file: $file - ${e.message}")
		}
	}
}