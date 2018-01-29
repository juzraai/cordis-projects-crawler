package com.github.juzraai.cordis.projects

import com.beust.jcommander.*
import com.github.juzraai.cordis.projects.cli.*
import mu.*

/**
 * @author Zsolt Jurányi
 */
fun main(args: Array<String>) {
	CordisProjectsCrawler().start(args)
	// Debug:
	//val a: Array<String> = "-s 1..5".split(' ').toTypedArray()
	//CordisProjectsCrawler().start(a)
}

class CordisProjectsCrawler {

	// TODO builder -> start(config)

	companion object : KLogging()

	fun start(args: Array<String>) {
		var configuration = CpcConfiguration()
		with(JCommander.newBuilder().addObject(configuration).build()) {
			try {
				parse(*args)
				start(configuration)
			} catch (e: Exception) {
				println(e.message + "\n")
				usage()
			}
		}
	}

	fun start(configuration: CpcConfiguration) {
		iterate(configuration)
				.forEach { println(it) }
	}

	fun iterate(configuration: CpcConfiguration): Sequence<Long> {
		with(configuration.scope ?: "") {
			return when {
				matches(Regex("\\d+")) -> sequenceOf(toLong())
				matches(Regex("\\d+\\.\\.\\d+")) -> {
					val bounds = split(Regex("\\.\\.")).map(String::toLong).sorted()
					LongRange(bounds[0], bounds[1]).asSequence()
				}
				equals("all", true) -> throw UnsupportedOperationException("TODO: fetch RCNs")
				equals("dir", true) -> throw UnsupportedOperationException("TODO: iterate config.directory")
				else -> {
					throw IllegalArgumentException("Invalid scope: $this")
				}
			}
		}
	}
}