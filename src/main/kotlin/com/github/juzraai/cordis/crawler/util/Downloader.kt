package com.github.juzraai.cordis.crawler.util

import mu.*
import org.jsoup.*

/**
 * @author Zsolt Jurányi
 */
class Downloader {

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
		val domain = url.replace(Regex(".*://"), "").replace(Regex("/.*"), "")
		smartSleep(sleep, domain)
		logger.trace("Fetching URL: $url")
		var body: String? = null
		try {
			val ts = -System.currentTimeMillis()
			body = Jsoup.connect(url)
					.followRedirects(true)
					.ignoreContentType(true)
					.maxBodySize(10_000_000)
					.timeout(60_000)
					.execute()
					.body()
			logger.trace("Got response in ${ts + System.currentTimeMillis()} ms, length: ${body?.length}")
		} catch (e: Exception) {
			logger.warn("Could not download URL: $url: ${e.message}")
		}
		lastReqTs[domain] = System.currentTimeMillis()
		validator?.run {
			if (null == body || !invoke(body!!)) {
				logger.warn("Invalid response from URL: $url")
				body = null
			}
		}
		return body
	}

	private fun smartSleep(sleep: Int, domain: String) {
		val lastReq = lastReqTs[domain] ?: 0
		val time = System.currentTimeMillis()
		val ms = Math.max(0, lastReq - time + sleep)
		if (0 < ms) {
			logger.trace("Sleeping $ms ms before downloading from $domain")
			Thread.sleep(ms)
		}
	}
}
