package com.github.juzraai.cordis.crawler.modules

import com.github.juzraai.cordis.crawler.*
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

	val seeds = mutableListOf(
			CordisProjectRcnSeed(),
			CordisProjectRcnRangeSeed(),
			CordisProjectRcnListSeed(),
			CordisProjectUrlSeed(),
			CordisProjectSearchUrlSeed(),
			CordisProjectRcnDirectorySeed(),
			AllCordisProjectRcnSeed()
	)

	val readers = mutableListOf(
			CordisProjectXmlFileCache(),
			CordisProjectXmlDownloader()
	)

	private fun allModules() = listOf(seeds, readers).flatten()

	fun close() {
		allModules().onEach {
			if (it is Closeable) try {
				it.close()
			} catch (e: IOException) {
				logger.error("Could not close module: ${it.javaClass.name}", e)
			}
		}
	}

	fun initialize(configuration: CordisCrawlerConfiguration) {
		allModules().onEach { it.configuration = configuration }
	}
}