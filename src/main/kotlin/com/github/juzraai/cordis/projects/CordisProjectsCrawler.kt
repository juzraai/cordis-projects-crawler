package com.github.juzraai.cordis.projects

import com.beust.jcommander.*
import com.github.juzraai.cordis.projects.cli.*
import com.github.juzraai.cordis.projects.read.*
import com.github.juzraai.cordis.projects.seed.*
import mu.*
import org.apache.log4j.*

/**
 * @author Zsolt Jurányi
 */
fun main(args: Array<String>) {
	//CordisProjectsCrawler().start(args)
	// Debug:
	val a: Array<String> = "-s 213190 -v".split(' ').toTypedArray()
	CordisProjectsCrawler().start(a)
}

class CordisProjectsCrawler {

	companion object : KLogging()

	var configuration = CpcConfiguration()

	val readers = mutableListOf(
			CordisXmlFileCache(),
			CordisXmlDownloader()
	)

	val seedGenerators = mutableListOf(
			SingleRcnSeed(),
			RcnRangeSeed(),
			DirectorySeed(),
			AllRcnsSeed()
	)

	fun start(args: Array<String>) {
		with(JCommander.newBuilder().addObject(configuration).build()) {
			try {
				parse(*args)
				start()
			} catch (e: Exception) {
				println(e.message + "\n")
				usage()
			}
		}
	}

	fun start() {
		setupLoggers()
		var t = -System.currentTimeMillis()
		val c = seed().onEach { logger.info("Processing RCN: $it") }
				.mapNotNull(this::read)
				.onEach(this::cache)
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

	private fun read(rcn: Long) = readers.asSequence()
			.mapNotNull { it.readCordisXmlByRcn(rcn, configuration) }
			.map { Pair(rcn, it) }
			.firstOrNull()

	private fun cache(data: Pair<Long, String>) {
		logger.trace("Caching XML")
		readers.mapNotNull { it as? ICordisXmlCache }
				.onEach { it.storeCordisXmlForRcn(data.first, data.second, configuration) }
	}
}