package com.github.juzraai.cordis.projects.cli

import com.beust.jcommander.*

/**
 * @author Zsolt Jurányi
 */
data class CpcConfiguration(
		// TODO builder
		// TODO make it open (kotlin-allopen?)

		@Parameter(names = ["-d", "--directory"])
		var directory: String = "cordis-data",

		@Parameter(names = ["-s", "--seed"])
		var seed: String? = null
)