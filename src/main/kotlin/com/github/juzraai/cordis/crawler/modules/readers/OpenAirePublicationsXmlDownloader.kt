package com.github.juzraai.cordis.crawler.modules.readers

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.model.cordis.*
import mu.*
import org.jsoup.*

/**
 * @author Zsolt Jurányi
 */
class OpenAirePublicationsXmlDownloader(override var configuration: CordisCrawlerConfiguration? = null) : IOpenAirePublicationsXmlReader {

	// TODO ? merge 2 downloaders into DefaultDownloader, then we can calc and print stats (reqs, sizes) (close method)

	companion object : KLogging()

	override fun publicationsXmlByProject(project: Project): String? {
		val url = publicationsXmlUrlByReference(project.reference!!)
		return try {
			logger.trace("Fetching publications XML: $url")
			Thread.sleep(2000) // TODO more sophisticated
			Jsoup.connect(url).maxBodySize(10_000_000).timeout(60_000).execute().body()
		} catch (e: Exception) {
			logger.warn("Failed to fetch URL: $url - ${e.message}")
			null
		}
	}

	private fun publicationsXmlUrlByReference(reference: String) =
			"http://api.openaire.eu/search/publications?projectID=$reference&model=sygma&size=10000"
}