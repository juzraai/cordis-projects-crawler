package com.github.juzraai.cordis.crawler.model

import com.github.juzraai.cordis.crawler.model.cordis.*
import com.github.juzraai.cordis.crawler.model.openaire.sygma.*

/**
 * @author Zsolt Jurányi
 */
open class CordisProject(
		val rcn: Long,
		var project: Project? = null,
		// TODO [v2.1] val result: Result? = null,
		var publications: List<Publication>? = null
		// TODO [v2.1] unified model
) {
	override fun toString(): String {
		return "CordisProject(rcn=$rcn, project=$project, publications=$publications)"
	}
}