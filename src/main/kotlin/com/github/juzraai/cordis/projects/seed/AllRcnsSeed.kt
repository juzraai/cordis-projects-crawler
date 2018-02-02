package com.github.juzraai.cordis.projects.seed

import com.github.juzraai.cordis.projects.cli.*

/**
 * @author Zsolt Jurányi
 */
class AllRcnsSeed : IRcnSeedGenerator {

	override fun generateRcns(scope: String?, configuration: CpcConfiguration): Sequence<Long>? {
		// TODO fetch all RCNs from CORDIS
		return null
	}
}