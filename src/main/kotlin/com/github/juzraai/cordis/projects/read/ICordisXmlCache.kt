package com.github.juzraai.cordis.projects.read

import com.github.juzraai.cordis.projects.cli.*

/**
 * @author Zsolt Jurányi
 */
interface ICordisXmlCache : ICordisXmlReader {

	fun storeCordisXmlForRcn(rcn: Long, xml: String, configuration: CpcConfiguration)
}