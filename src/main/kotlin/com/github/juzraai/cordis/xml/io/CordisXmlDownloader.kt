package com.github.juzraai.cordis.xml.io

import mu.*
import org.jsoup.*

/**
 * @author Zsolt Jurányi
 */
class CordisXmlDownloader : ICordisXmlReader {

	// TODO later: able to download result XML too (".../result/rcn/...")

	companion object : KLogging()

	override fun readCordisXmlByRcn(rcn: Long): String? {
		val url = url(rcn)
		return try {
			logger.trace("Fetching XML: $url")
			Thread.sleep(2000)
			Jsoup.connect(url).maxBodySize(10_000_000).timeout(60_000).execute().body()
		} catch (e: Exception) {
			logger.warn("Failed to fetch URL: $url - ${e.message}")
			null
		}
	}

	private fun url(rcn: Long) = "https://cordis.europa.eu/project/rcn/${rcn}_en.xml"
}