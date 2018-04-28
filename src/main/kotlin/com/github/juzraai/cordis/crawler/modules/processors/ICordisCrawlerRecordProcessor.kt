package com.github.juzraai.cordis.crawler.modules.processors

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.modules.*

/**
 * @author Zsolt Jurányi
 */
interface ICordisCrawlerRecordProcessor : ICordisCrawlerModule {

	fun process(cordisCrawlerRecord: CordisCrawlerRecord): CordisCrawlerRecord?
}