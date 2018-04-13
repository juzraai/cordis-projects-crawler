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
			CordisProjectRcnRangeSeed(),
			CordisProjectRcnListSeed(),
			CordisProjectUrlSeed(),
			AllCordisProjectRcnSeed(), // rewrites to search URL
			CordisProjectSearchUrlSeed(),
			CordisProjectRcnDirectorySeed(),

			// processors
			CordisProjectCrawler(),
			OpenAirePublicationsCrawler(),
			// TODO [v2.1] project documents downloader (webItems)
			// TODO [v2.1] project results crawler
			// TODO [v2.1] unified model generator?

			// readers
			CordisCrawlerFileCache(),
			CordisProjectXmlDownloader(),
			OpenAirePublicationsXmlDownloader(),

			// parsers
			CordisProjectXmlParser(),
			OpenAirePublicationsXmlParser(),

			// exporters
			ProjectsTsvExporter(),
			PublicationsTsvExporter(),
			CordisProjectMysqlExporter()
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
			it.initialize(configuration, this)
		}
	}

	@Suppress("UNCHECKED_CAST")
	fun <I : ICordisCrawlerModule> ofType(type: Class<I>) =
			modules.filter { type.isInstance(it) }.mapNotNull { it as? I }
}