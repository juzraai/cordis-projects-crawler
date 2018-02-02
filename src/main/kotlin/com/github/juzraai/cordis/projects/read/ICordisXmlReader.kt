package com.github.juzraai.cordis.projects.read

import com.github.juzraai.cordis.projects.cli.*

/**
 * @author Zsolt Jurányi
 */
interface ICordisXmlReader {

	fun readCordisXmlByRcn(rcn: Long, configuration: CpcConfiguration): String?
}