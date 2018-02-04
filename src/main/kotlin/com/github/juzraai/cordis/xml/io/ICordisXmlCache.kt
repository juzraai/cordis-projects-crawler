package com.github.juzraai.cordis.xml.io

/**
 * @author Zsolt Jurányi
 */
interface ICordisXmlCache : ICordisXmlReader {

	fun storeCordisXmlForRcn(rcn: Long, xml: String)
}