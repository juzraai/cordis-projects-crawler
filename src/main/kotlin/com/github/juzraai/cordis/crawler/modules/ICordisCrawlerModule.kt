package com.github.juzraai.cordis.crawler.modules

import com.github.juzraai.cordis.crawler.model.*

/**
 * @author Zsolt Jurányi
 */
interface ICordisCrawlerModule {

	fun initialize(configuration: CordisCrawlerConfiguration) {} // TODO receive modules too?
}