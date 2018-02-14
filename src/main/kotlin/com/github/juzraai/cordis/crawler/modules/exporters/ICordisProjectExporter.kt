package com.github.juzraai.cordis.crawler.modules.exporters

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.modules.*

/**
 * @author Zsolt Jurányi
 */
interface ICordisProjectExporter : ICordisCrawlerModule {

	fun exportCordisProjects(cordisProjects: List<CordisProject>)
}