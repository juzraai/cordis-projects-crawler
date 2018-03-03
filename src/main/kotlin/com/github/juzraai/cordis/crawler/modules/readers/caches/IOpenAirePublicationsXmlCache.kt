package com.github.juzraai.cordis.crawler.modules.readers.caches

import com.github.juzraai.cordis.crawler.model.cordis.*
import com.github.juzraai.cordis.crawler.modules.readers.*

/**
 * @author Zsolt Jurányi
 */
interface IOpenAirePublicationsXmlCache : IOpenAirePublicationsXmlReader {

	fun cachePublicationsXml(xml: String, project: Project)
}