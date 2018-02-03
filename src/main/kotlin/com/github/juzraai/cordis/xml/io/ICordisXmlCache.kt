package com.github.juzraai.cordis.xml.io

import com.github.juzraai.cordis.crawler.*

/**
 * @author Zsolt Jurányi
 */
interface ICordisXmlCache : ICordisXmlReader {

	fun storeCordisXmlForRcn(rcn: Long, xml: String, configuration: CordisCrawlerConfiguration)
}