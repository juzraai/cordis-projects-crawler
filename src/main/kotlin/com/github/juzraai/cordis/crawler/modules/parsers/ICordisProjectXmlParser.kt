package com.github.juzraai.cordis.crawler.modules.parsers

import com.github.juzraai.cordis.crawler.modules.*
import com.github.juzraai.cordis.crawler.model.cordis.*

/**
 * @author Zsolt Jurányi
 */
interface ICordisProjectXmlParser: ICordisCrawlerModule {

	fun parseProjectXml(xml: String): Project?
}