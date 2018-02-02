package com.github.juzraai.cordis.projects.read

import com.github.juzraai.cordis.projects.cli.*

/**
 * @author Zsolt Jurányi
 */
class CordisXmlCacheReader : ICordisXmlReader {

	override fun readCordisXmlByRcn(rcn: Long, configuration: CpcConfiguration): String? {
		// TODO read from cache
		return null
	}
}