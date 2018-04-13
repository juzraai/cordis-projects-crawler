package com.github.juzraai.cordis.crawler.modules.processors

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.modules.*

/**
 * @author Zsolt Jurányi
 */
interface ICordisProjectProcessor : ICordisCrawlerModule {

	fun process(cordisProject: CordisProject): CordisProject?
}