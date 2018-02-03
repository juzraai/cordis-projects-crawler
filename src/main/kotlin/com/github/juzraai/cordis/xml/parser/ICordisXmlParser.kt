package com.github.juzraai.cordis.xml.parser

import com.github.juzraai.cordis.xml.model.*

/**
 * @author Zsolt Jurányi
 */
interface ICordisXmlParser {

	fun parseCordisXml(xml: String): CordisXml?
}