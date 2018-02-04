package com.github.juzraai.cordis.xml.model

import org.simpleframework.xml.*

/**
 * @author Zsolt Jurányi
 */
@Default(required = false)
data class Programme(
		@field:Attribute(required = false)
		var type: String? = null,

		var availableLanguages: String? = null,
		var code: String? = null,
		var frameworkProgramme: String? = null,

		@field:Path("parent")
		@field:Element(name = "programme", required = false)
		var parent: Programme? = null,

		var pga: String? = null,
		var rcn: Long? = null,
		var relations: Relations? = null,
		var shortTitle: String? = null,
		var title: String? = null,
		var url: String? = null
)