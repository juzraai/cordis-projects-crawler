package com.github.juzraai.cordis.crawler.modules

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.modules.exporters.*
import com.github.juzraai.cordis.crawler.modules.parsers.*
import com.github.juzraai.cordis.crawler.modules.processors.*
import com.github.juzraai.cordis.crawler.modules.readers.*
import com.github.juzraai.cordis.crawler.modules.readers.caches.*
import com.github.juzraai.cordis.crawler.modules.seeds.*
import mu.*
import java.io.*

/**
 * @author Zsolt Jurányi
 */
class CordisCrawlerModuleRegistry {

	companion object : KLogging()

	val modules = mutableListOf<ICordisCrawlerModule>(

			// seeds
			CordisProjectRcnSeed(),
			CordisProjectRcnRangeSeed(),
			CordisProjectRcnListSeed(),
			CordisProjectUrlSeed(),
			CordisProjectSearchUrlSeed(),
			CordisProjectRcnDirectorySeed(),
			AllCordisProjectRcnSeed(),

			// processors
			CordisProjectCrawler(this),
			OpenAirePublicationsCrawler(this),
			// TODO project documents downloader (webItems)
			// TODO project results crawler
			// TODO unified model generator?

			// readers
			CordisProjectXmlFileCache(),
			CordisProjectXmlDownloader(),
			OpenAirePublicationsXmlCache(),
			OpenAirePublicationsXmlDownloader(),

			// parsers
			CordisProjectXmlParser(),
			OpenAirePublicationsXmlParser(),

			// exporters
			ProjectsTsvExporter(),
			PublicationsTsvExporter()
			// TODO MySqlExporter()
	)

	fun close() {
		modules.onEach {
			if (it is Closeable) try {
				logger.trace("Closing module: ${it.javaClass.name}")
				it.close()
			} catch (e: IOException) {
				logger.error("Could not close module: ${it.javaClass.name}", e)
			}
		}
	}

	fun initialize(configuration: CordisCrawlerConfiguration) {
		modules.onEach {
			logger.trace("Initializing module: ${it.javaClass.name}")
			it.initialize(configuration)
		}
	}

	@Suppress("UNCHECKED_CAST")
	fun <I : ICordisCrawlerModule> ofType(type: Class<I>) =
			modules.filter { type.isInstance(it) }.mapNotNull { it as? I }
}