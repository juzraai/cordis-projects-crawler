package com.github.juzraai.cordis.crawler.modules.readers

import com.github.juzraai.cordis.crawler.model.cordis.*
import com.github.juzraai.cordis.crawler.util.*

/**
 * @author Zsolt Jurányi
 */
class OpenAirePublicationsXmlDownloader : IOpenAirePublicationsXmlReader {

	override fun publicationsXmlByProject(project: Project) =
			Downloader().download(publicationsXmlUrlByReference(project.reference!!)) {
				it.startsWith("<?xml")
			}

	private fun publicationsXmlUrlByReference(reference: String) =
			"http://api.openaire.eu/search/publications?projectID=$reference&model=sygma&size=10000"
}