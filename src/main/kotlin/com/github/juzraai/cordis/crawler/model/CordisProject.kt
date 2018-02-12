package com.github.juzraai.cordis.crawler.model

import com.github.juzraai.cordis.crawler.model.cordis.*

/**
 * @author Zsolt Jurányi
 */
data class CordisProject(
		val rcn: Long,
		var project: Project? = null //,
		//val result: Result? = null
)