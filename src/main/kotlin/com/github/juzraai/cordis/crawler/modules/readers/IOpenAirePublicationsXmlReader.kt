package com.github.juzraai.cordis.crawler.modules.readers

import com.github.juzraai.cordis.crawler.model.cordis.*
import com.github.juzraai.cordis.crawler.modules.*

/**
 * @author Zsolt Jurányi
 */
interface IOpenAirePublicationsXmlReader : ICordisCrawlerModule {

	fun publicationsXmlByProject(project: Project): String?
}