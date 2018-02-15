package com.github.juzraai.cordis.crawler

import com.beust.jcommander.*
import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.modules.*
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

	fun crawlProjects(seed: Sequence<Long>? = null) {
		try {
			setupLoggers()
			logger.trace("Configuration: $configuration")
			logger.info("Initializing modules")
			modules.initialize(configuration)

			var t = -System.currentTimeMillis()
			var allCount = 0L
			val processedCount = (seed ?: seed())
					.onEach {
						logger.info("Processing project RCN: $it")
						allCount++
					}
					.map { CordisProject(it) }
					.mapNotNull(this::process)
					.chunked(100) // TODO config
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
			level = if (configuration.verbose) Level.TRACE else Level.INFO
		}
	}

	private fun seed() = modules.seeds.asSequence()
			.mapNotNull(ICordisProjectRcnSeed::projectRcns)
			.firstOrNull()
			?: throw UnsupportedOperationException("Invalid seed: ${configuration.seed}")

	private fun process(cordisProject: CordisProject): CordisProject? {
		var r: CordisProject? = cordisProject
		modules.processors.onEach { p ->
			r?.also {
				r = p.process(it)
				// TODO catch exception, log error, r = null?
			}
		}
		return r
	}

	private fun export(cordisProjects: List<CordisProject>) {
		modules.exporters.onEach {
			it.exportCordisProjects(cordisProjects)
			// TODO catch exception, log error
		}
	}
}