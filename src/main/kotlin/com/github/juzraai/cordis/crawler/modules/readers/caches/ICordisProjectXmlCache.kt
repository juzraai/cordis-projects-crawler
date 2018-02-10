package com.github.juzraai.cordis.crawler.modules.readers.caches

import com.github.juzraai.cordis.crawler.modules.readers.*

/**
 * @author Zsolt Jurányi
 */
interface ICordisProjectXmlCache : ICordisProjectXmlReader {

	fun cacheProjectXml(rcn: Long, xml: String)
}