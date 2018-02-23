package com.github.juzraai.cordis.crawler.util

import mu.*
import org.jsoup.*

/**
 * @author Zsolt Jurányi
 */
class Downloader {

	// TODO as module? then it would be replaceable. also a close method could print stats

	companion object : KLogging() {
		private val initialSleep = 2000
		private val lastReqTs = mutableMapOf<String, Long>()
		private val tries = 5
	}

	fun download(url: String, validator: ((String) -> Boolean)? = null): String? {
		var body: String?
		var t = 0
		do {
			body = downloadOnce(++t * initialSleep, url, validator)
		} while (null == body && t <= tries)
		return body
	}

	private fun downloadOnce(sleep: Int, url: String, validator: ((String) -> Boolean)?): String? {
		smartSleep(sleep, url)
		logger.trace("Fetching URL: $url")
		var body: String? = null
		try {
			body = Jsoup.connect(url)
					.followRedirects(true)
					.ignoreContentType(true)
					.maxBodySize(10_000_000)
					.timeout(60_000)
					.execute()
					.body()
		} catch (e: Exception) {
			logger.warn("Could not download URL: $url: ${e.message}")
		}
		validator?.run {
			if (null == body || !invoke(body!!)) {
				logger.warn("Invalid response from URL: $url")
				body = null
			}
		}
		return body
	}

	private fun smartSleep(sleep: Int, url: String) {
		val domain = url.replace(Regex(".*://"), "").replace(Regex("/.*"), "")
		val lastReq = lastReqTs[domain] ?: 0
		val time = System.currentTimeMillis()
		val ms = Math.max(0, lastReq - time + sleep)
		if (0 < ms) {
			logger.trace("Sleeping $ms ms before downloading from $domain")
			Thread.sleep(ms)
		}
		lastReqTs[domain] = System.currentTimeMillis()
	}
}
