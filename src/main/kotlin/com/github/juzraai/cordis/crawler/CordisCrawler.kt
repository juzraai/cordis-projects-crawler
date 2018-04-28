package com.github.juzraai.cordis.crawler

import com.beust.jcommander.*
import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.modules.*
import com.github.juzraai.cordis.crawler.modules.exporters.*
import com.github.juzraai.cordis.crawler.modules.processors.*
import com.github.juzraai.cordis.crawler.modules.seeds.*
import mu.*
import org.apache.log4j.*

/**
 * @author Zsolt Jurányi
 */
fun main(args: Array<String>) {
	CordisCrawler().crawlProjects(args)
}

class CordisCrawler(
		var configuration: CordisCrawlerConfiguration = CordisCrawlerConfiguration(),
		var modules: CordisCrawlerModuleRegistry = CordisCrawlerModuleRegistry()
) {

	companion object : KLogging()

	fun crawlProjects(args: Array<String>) {
		with(JCommander.newBuilder().addObject(configuration).build()) {
			try {
				parse(*args)
			} catch (e: Exception) {
				println(e.message + "\n")
				usage()
				return
			}
		}
		crawlProjects()
	}

	fun crawlProjects(seed: Iterator<Long>? = null) {
		try {
			setupLoggers()
			logger.trace("Configuration: $configuration")
			logger.info("Initializing modules")
			modules.initialize(configuration)

			var t = -System.currentTimeMillis()
			var allCount = 0L
			val processedCount = (seed ?: seed()).asSequence()
					.onEach {
						logger.info("Processing project RCN: $it")
						allCount++
					}
					.map { CordisCrawlerRecord(it) }
					.mapNotNull(this::process)
					.chunked(100) // TODO [v2.1] config
					.onEach(this::export)
					.flatten()
					.count() // <-- need to run the operations on Sequence
			t += System.currentTimeMillis()
			logger.info("Processed $processedCount/$allCount projects in ${t / 1000.0} seconds")
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
			level = when {
				configuration.verbose -> Level.TRACE
				configuration.quiet -> Level.OFF
				else -> Level.INFO
			}
		}
	}

	private fun seed() = modules.ofType(ICordisProjectRcnSeed::class.java).asSequence()
			.mapNotNull(ICordisProjectRcnSeed::projectRcns)
			.firstOrNull()
			?: throw UnsupportedOperationException("Invalid seed: ${configuration.seed}")

	private fun process(cordisCrawlerRecord: CordisCrawlerRecord): CordisCrawlerRecord? {
		var r: CordisCrawlerRecord? = cordisCrawlerRecord
		modules.ofType(ICordisCrawlerRecordProcessor::class.java).onEach { p ->
			r?.also {
				try {
					r = p.process(it)
					if (null == r) logger.trace("Project RCN ${cordisCrawlerRecord.rcn} dropped by ${p.javaClass.name}")
				} catch (e: Exception) {
					logger.error("Could not process ${cordisCrawlerRecord.rcn} with ${p.javaClass.name}", e)
				}
			}
		}
		return r
	}

	private fun export(cordisCrawlerRecords: List<CordisCrawlerRecord>) {
		modules.ofType(ICordisCrawlerRecordExporter::class.java).onEach {
			try {
				it.export(cordisCrawlerRecords)
			} catch (e: Exception) {
				logger.error("Error while exporting data with ${it.javaClass.name}", e)
			}
		}
	}
}