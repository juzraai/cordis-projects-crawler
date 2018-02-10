package com.github.juzraai.cordis.crawler.modules.readers

import com.github.juzraai.cordis.crawler.modules.*

/**
 * @author Zsolt Jurányi
 */
interface ICordisProjectXmlReader : ICordisCrawlerModule {

	fun projectXmlByRcn(rcn: Long): String?
}