package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.model.*

/**
 * @author Zsolt Jurányi
 */
class AllCordisProjectRcnSeed : ICordisProjectRcnSeed {

	private var configuration: CordisCrawlerConfiguration? = null

	override fun initialize(configuration: CordisCrawlerConfiguration) {
		this.configuration = configuration
	}

	override fun projectRcns() = if ("all".equals(configuration?.seed, true)) {
		// TODO fetch all project RCNs from CORDIS
		// https://cordis.europa.eu/projects/result_en?q=contenttype%3D%27project%27&num=100&srt=/project/contentUpdateDate:decreasing&format=csv
		// + &p=1
		// until only header comes
		throw UnsupportedOperationException()
	} else null
}