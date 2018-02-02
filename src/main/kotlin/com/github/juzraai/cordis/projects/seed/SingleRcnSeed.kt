package com.github.juzraai.cordis.projects.seed

import com.github.juzraai.cordis.projects.cli.*

/**
 * @author Zsolt Jurányi
 */
class SingleRcnSeed : IRcnSeedGenerator {

	override fun generateRcns(scope: String?, configuration: CpcConfiguration) =
			if (null != scope && scope.matches(Regex("\\d+"))) sequenceOf(scope.toLong())
			else null
}