package com.github.juzraai.cordis.crawler.modules.readers

import com.github.juzraai.cordis.crawler.model.*
import mu.*
import org.jsoup.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectXmlDownloader(override var configuration: CordisCrawlerConfiguration? = null) : ICordisProjectXmlReader {

	// TODO later?: implement ICordisResultXmlReader and download result XML by RCN (".../result/rcn/...")

	companion object : KLogging()

	override fun projectXmlByRcn(rcn: Long): String? {
		val url = projectXmlUrlByRcn(rcn)
		return try {
			logger.trace("Fetching project XML: $url")
			Thread.sleep(2000)
			Jsoup.connect(url).maxBodySize(10_000_000).timeout(60_000).execute().body()
		} catch (e: Exception) {
			logger.warn("Failed to fetch URL: $url - ${e.message}")
			null
		}
	}

	private fun projectXmlUrlByRcn(rcn: Long) = "https://cordis.europa.eu/project/rcn/${rcn}_en.xml"
}