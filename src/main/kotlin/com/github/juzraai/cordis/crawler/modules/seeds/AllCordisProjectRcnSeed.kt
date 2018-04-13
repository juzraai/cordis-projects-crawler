package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.modules.*

/**
 * @author Zsolt Jurányi
 */
class AllCordisProjectRcnSeed : ICordisProjectRcnSeed {

	private var configuration: CordisCrawlerConfiguration? = null

	override fun initialize(configuration: CordisCrawlerConfiguration, modules: CordisCrawlerModuleRegistry) {
		this.configuration = configuration
	}

	override fun projectRcns(): Iterator<Long>? {
		configuration?.apply {
			if ("all".equals(seed, true))
				seed = "https://cordis.europa.eu/projects/result_en?q=contenttype%3D%27project%27"
		}
		return null
	}
}