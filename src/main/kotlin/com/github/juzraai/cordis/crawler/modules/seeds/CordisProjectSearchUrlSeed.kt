package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.util.*
import mu.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectSearchUrlSeed : ICordisProjectRcnSeed {

	private var configuration: CordisCrawlerConfiguration? = null

	override fun initialize(configuration: CordisCrawlerConfiguration) {
		this.configuration = configuration
	}

	override fun projectRcns() = (configuration?.seed ?: "").run {
		if (matches(Regex("https://cordis\\.europa\\.eu/(projects|search)/result_en\\?.*"))) {
			CordisProjectSearchResultIterator(this)
		} else null
	}

	class CordisProjectSearchResultIterator(seedUrl: String) : Iterator<Long> {

		companion object : KLogging() {
			const val num = 100 // CORDIS options: 10/20/30/50/100, default: 10
			const val UTF8_BOM = "\uFEFF"
		}

		var url = seedUrl.replace(Regex("[?&](format|num|p|src)=[^&]+"), "")
		var end = false
		var p = 0
		var rcns: List<Long>? = null
		var rcnsIterator: Iterator<Long>? = null

		init {
			logger.info("Crawling CORDIS list: $url")
			url += "&format=csv&num=$num&srt=/project/contentUpdateDate:decreasing"
		}

		override fun hasNext(): Boolean {
			if (!end && rcnsIterator?.hasNext() != true) crawl()
			return rcnsIterator?.hasNext() ?: false
		}

		override fun next(): Long {
			if (!hasNext()) throw NoSuchElementException()
			return rcnsIterator!!.next()
		}

		private fun crawl() {
			++p
			val csv = Downloader().download("$url&p=$p") {
				it.startsWith("$UTF8_BOM\"Content type\";\"Record Number\";")
			}
			rcns = null
			rcnsIterator = null
			var c = 0
			if (null != csv) {
				rcns = csv.split("\n")
						.onEach { ++c }
						.filter { it.matches(Regex("project;\\d++;.*")) }
						.map { it.split(";")[1].toLong() }
						.toList()
				rcnsIterator = rcns!!.iterator()
			}
			logger.trace("Received $c lines, including ${rcns?.size ?: 0} project RCNs")
			if (null == rcnsIterator || c <= 2) {
				end = true
				logger.trace("Considering this as the last page")
			}
		}
	}

}