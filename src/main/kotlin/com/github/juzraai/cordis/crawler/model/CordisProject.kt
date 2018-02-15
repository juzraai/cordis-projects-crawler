package com.github.juzraai.cordis.crawler.model

import com.github.juzraai.cordis.crawler.model.cordis.*
import com.github.juzraai.cordis.crawler.model.openaire.sygma.*

/**
 * @author Zsolt Jurányi
 */
data class CordisProject( // TODO make it open!
		val rcn: Long,
		var project: Project? = null,
		// TODO val result: Result? = null,
		var publications: List<Publication>? = null
		// TODO unified model
)