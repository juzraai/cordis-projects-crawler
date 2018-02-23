package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.modules.*

/**
 * @author Zsolt Jurányi
 */
interface ICordisProjectRcnSeed : ICordisCrawlerModule {

	fun projectRcns(): Iterator<Long>?
}