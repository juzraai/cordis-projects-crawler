package com.github.juzraai.cordis.projects

import com.beust.jcommander.*
import com.github.juzraai.cordis.projects.cli.*
import com.github.juzraai.cordis.projects.read.*
import com.github.juzraai.cordis.projects.seed.*
import mu.*

/**
 * @author Zsolt Jurányi
 */
fun main(args: Array<String>) {
	CordisProjectsCrawler().start(args)
	// Debug:
	//val a: Array<String> = "-s 213190".split(' ').toTypedArray()
	//CordisProjectsCrawler().start(a)
}

class CordisProjectsCrawler {

	companion object : KLogging()

	var configuration = CpcConfiguration()

	val readers = mutableListOf(
			CordisXmlCacheReader(),
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
		seed()
				.mapNotNull(this::read)
				.forEach { println(it) }
	}

	private fun seed() = seedGenerators.asSequence()
			.mapNotNull { it.generateRcns(configuration.seed, configuration) }
			.firstOrNull()
			?: throw UnsupportedOperationException("Invalid seed: ${configuration.seed}")

	private fun read(rcn: Long) = readers.asSequence()
			.mapNotNull { it.readCordisXmlByRcn(rcn, configuration) }
			.firstOrNull()
}