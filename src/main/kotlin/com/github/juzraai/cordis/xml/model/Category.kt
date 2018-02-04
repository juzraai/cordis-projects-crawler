package com.github.juzraai.cordis.xml.model

import org.simpleframework.xml.*

/**
 * @author Zsolt Jurányi
 */
@Default(required = false)
data class Category(
		@field:Attribute
		var classification: String? = null,
		@field:Attribute
		var type: String? = null,

		var availableLanguages: String? = null,
		var code: String? = null,
		var title: String? = null
)