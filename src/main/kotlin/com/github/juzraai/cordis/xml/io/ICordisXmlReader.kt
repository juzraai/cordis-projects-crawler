package com.github.juzraai.cordis.xml.io

import com.github.juzraai.cordis.crawler.*

/**
 * @author Zsolt Jurányi
 */
interface ICordisXmlReader {

	fun readCordisXmlByRcn(rcn: Long, configuration: CordisCrawlerConfiguration): String?
}