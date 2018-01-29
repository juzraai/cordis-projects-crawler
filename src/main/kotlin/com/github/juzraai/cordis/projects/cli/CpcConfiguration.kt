package com.github.juzraai.cordis.projects.cli

import com.beust.jcommander.*

/**
 * @author Zsolt Jurányi
 */
data class CpcConfiguration(

		@Parameter(names = ["-d", "--directory"])
		var directory: String = "cordis-data",

		@Parameter(names = ["-s", "--scope"])
		var scope: String? = null
)