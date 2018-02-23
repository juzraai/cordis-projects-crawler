package com.github.juzraai.cordis.crawler.modules.readers

import com.github.juzraai.cordis.crawler.util.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectXmlDownloader : ICordisProjectXmlReader {

	override fun projectXmlByRcn(rcn: Long) =
			Downloader().download(projectXmlUrlByRcn(rcn)) {
				it.startsWith("<?xml")
			}

	private fun projectXmlUrlByRcn(rcn: Long) = "https://cordis.europa.eu/project/rcn/${rcn}_en.xml"
}