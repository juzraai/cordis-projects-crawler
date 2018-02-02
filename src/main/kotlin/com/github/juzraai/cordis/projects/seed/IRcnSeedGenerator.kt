package com.github.juzraai.cordis.projects.seed

import com.github.juzraai.cordis.projects.cli.*

/**
 * @author Zsolt Jurányi
 */
interface IRcnSeedGenerator {

	fun generateRcns(scope: String?, configuration: CpcConfiguration): Sequence<Long>?
}