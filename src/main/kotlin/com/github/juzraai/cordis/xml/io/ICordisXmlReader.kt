package com.github.juzraai.cordis.xml.io

/**
 * @author Zsolt Jurányi
 */
interface ICordisXmlReader {

	fun readCordisXmlByRcn(rcn: Long): String?
}