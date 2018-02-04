package com.github.juzraai.cordis.crawler

import com.beust.jcommander.*
import com.github.juzraai.cordis.crawler.seed.*
import com.github.juzraai.cordis.xml.io.*
import com.github.juzraai.cordis.xml.model.*
import com.github.juzraai.cordis.xml.parser.*
import mu.*
import org.apache.log4j.*

/**
 * @author Zsolt Jurányi
 */
fun main(args: Array<String>) {
	CordisCrawler().start(args)
}

class CordisCrawler(
		var configuration: CordisCrawlerConfiguration = CordisCrawlerConfiguration()
) {

	// TODO rename POM artifact to "cordis-crawler"
	// TODO rename repo accordingly

	companion object : KLogging()

	val readers = mutableListOf(
			CordisXmlFileCache(),
			CordisXmlDownloader()
	)

	val seedGenerators = mutableListOf(
			SingleRcnSeed(),
			RcnRangeSeed(),
			RcnListSeed(),
			DirectorySeed(),
			AllRcnsSeed()
	)

	val xmlParsers = mutableListOf<ICordisXmlParser>(
			CordisProjectXmlParser()
	)

	fun start(args: Array<String>) {
		with(JCommander.newBuilder().addObject(configuration).build()) {
			try {
				parse(*args)
			} catch (e: Exception) {
				println(e.message + "\n")
				usage()
				return
			}
		}
		start()
	}

	fun start(customProcessor: ((CordisXml) -> Unit)? = null) {
		start(seed(), customProcessor)
	}

	fun start(seed: Sequence<Long>, customProcessor: ((CordisXml) -> Unit)? = null) {
		setupLoggers()

		logger.info("Initializing modules")
		listOf(
				*readers.toTypedArray(),
				*seedGenerators.toTypedArray(),
				*xmlParsers.toTypedArray()
		).onEach { (it as? ICordisCrawlerConfigurationAware)?.configuration = configuration }

		var t = -System.currentTimeMillis()
		val c = seed.onEach { logger.info("Processing RCN: $it") }
				.mapNotNull(this::read)
				.onEach(this::cache)
				.mapNotNull(this::parse)
				.onEach { customProcessor?.invoke(it.second) }
				.count() // <-- need to run the operations on Sequence
		t += System.currentTimeMillis()
		logger.info("Processed $c RCNs in ${t / 1000.0} seconds")
	}

	private fun setupLoggers() {
		val layout = PatternLayout("%d{yyyy-MM-dd HH:mm:ss} [%-5p] %m%n")
		Logger.getRootLogger().apply {
			removeAllAppenders()
			addAppender(ConsoleAppender(layout, "System.err"))
			level = if (configuration.verbose) Level.TRACE else Level.INFO
		}
	}

	private fun seed() = seedGenerators.asSequence()
			.mapNotNull { it.generateRcns(configuration.seed, configuration) }
			.firstOrNull()
			?: throw UnsupportedOperationException("Invalid seed: ${configuration.seed}")

	private fun read(rcn: Long): Pair<Long, String>? {
		logger.trace("Reading XML: $rcn")
		return readers.asSequence()
				.mapNotNull { it.readCordisXmlByRcn(rcn) }
				.map { Pair(rcn, it) }
				.firstOrNull()
	}

	private fun cache(data: Pair<Long, String>) {
		logger.trace("Caching XML: ${data.first}")
		readers.mapNotNull { it as? ICordisXmlCache }
				.onEach { it.storeCordisXmlForRcn(data.first, data.second) }
	}

	private fun parse(data: Pair<Long, String>): Pair<Long, CordisXml>? {
		logger.trace("Parsing XML: ${data.first}")
		return xmlParsers.asSequence()
				.mapNotNull { it.parseCordisXml(data.second) }
				.mapNotNull { Pair(data.first, it) }
				.firstOrNull()
	}
}