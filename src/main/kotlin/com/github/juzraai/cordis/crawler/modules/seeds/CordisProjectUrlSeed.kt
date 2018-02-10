package com.github.juzraai.cordis.crawler.modules.seeds

import com.github.juzraai.cordis.crawler.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectUrlSeed(override var configuration: CordisCrawlerConfiguration? = null) : ICordisProjectRcnSeed {

	override fun projectRcns(): Sequence<Long>? {
		return (configuration?.seed ?: "").run {
			if (matches(Regex("http://cordis\\.europa\\.eu/project/rcn/\\d+_\\w+.(html|xml)"))) {
				sequenceOf(replace(Regex("\\D+"), "").toLong())
			} else null
		}
	}
}