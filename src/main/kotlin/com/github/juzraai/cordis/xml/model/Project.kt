package com.github.juzraai.cordis.xml.model

import org.simpleframework.xml.*

/**
 * @author Zsolt Jurányi
 */
@Root(name = "project")
@Default(required = false)
data class Project(
		var acronym: String? = null,
		var availableLanguages: String? = null,
		var language: String? = null,
		var objective: String? = null,
		var rcn: Long? = null,
		var reference: Long? = null,
		var teaser: String? = null,
		var title: String? = null
)
