package com.github.juzraai.cordis.crawler.modules.parsers

import com.github.juzraai.cordis.crawler.model.openaire.sygma.*
import com.github.juzraai.cordis.crawler.modules.*

/**
 * @author Zsolt Jurányi
 */
interface IOpenAirePublicationsXmlParser : ICordisCrawlerModule {

	fun parsePublicationsXml(xml: String): List<Publication>?
}