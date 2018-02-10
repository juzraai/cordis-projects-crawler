package com.github.juzraai.cordis.crawler

import com.beust.jcommander.*
import com.github.juzraai.cordis.crawler.modules.*
import com.github.juzraai.cordis.crawler.modules.readers.caches.*
import com.github.juzraai.cordis.crawler.modules.seeds.*
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
		var configuration: CordisCrawlerConfiguration = CordisCrawlerConfiguration(),
		var modules: CordisCrawlerModuleRegistry = CordisCrawlerModuleRegistry()
) {
	// TODO exporters for projects (JSON, CSV, MySQL)

	// TODO
	// - user seeds are project RCNs
	// - we need multiple engines:
	// -- mainTask: CordisProject(rcn, projectXml, resultXmls, publications) -> exporters
	// -- projectTask: record: rcn:Long -> Project -> project exporters
	// -- resultTask: record: rcn:Long -> Result -> result exporters
	// - main task calls projectTask, extracts result RCNs, runs resultTasks, exports
	// - CordisCrawlerContext: config + modules

	companion object : KLogging()

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
		try {
			setupLoggers()
			logger.info("Initializing modules")
			modules.initialize(configuration)

			var t = -System.currentTimeMillis()
			val c = seed.onEach { logger.info("Processing RCN: $it") }
					.mapNotNull(this::read)
					.onEach(this::cache)
					.mapNotNull(this::parse)
					.onEach { customProcessor?.invoke(it.second) }
					.count() // <-- need to run the operations on Sequence
			t += System.currentTimeMillis()
			logger.info("Processed $c RCNs in ${t / 1000.0} seconds")
		} catch (e: Exception) {
			logger.error("Error", e)
		} finally {
			logger.info("Turning off modules")
			modules.close()
		}
	}

	private fun setupLoggers() {
		val layout = PatternLayout("%d{yyyy-MM-dd HH:mm:ss} [%-5p] %m%n")
		Logger.getRootLogger().apply {
			removeAllAppenders()
			addAppender(ConsoleAppender(layout, "System.err"))
			level = if (configuration.verbose) Level.TRACE else Level.INFO
		}
	}

	private fun seed() = modules.seeds.asSequence()
			.mapNotNull(ICordisProjectRcnSeed::projectRcns)
			.firstOrNull()
			?: throw UnsupportedOperationException("Invalid seed: ${configuration.seed}")

	private fun read(rcn: Long): Pair<Long, String>? {
		logger.trace("Reading XML: $rcn")
		return modules.readers.asSequence()
				.mapNotNull { it.projectXmlByRcn(rcn) }
				.map { Pair(rcn, it) }
				.firstOrNull()
	}

	private fun cache(data: Pair<Long, String>) {
		logger.trace("Caching XML: ${data.first}")
		modules.readers.mapNotNull { it as? ICordisProjectXmlCache }
				.onEach { it.cacheProjectXml(data.first, data.second) }
	}

	private fun parse(data: Pair<Long, String>): Pair<Long, CordisXml>? {
		logger.trace("Parsing XML: ${data.first}")
		return xmlParsers.asSequence()
				.mapNotNull { it.parseCordisXml(data.second) }
				.mapNotNull { Pair(data.first, it) }
				.firstOrNull()
	}
}