package com.github.juzraai.cordis.crawler.model.cordis

import org.simpleframework.xml.*

/**
 * @author Zsolt Jurányi
 */
@Default(required = false)
data class WebItem(
		@field:Attribute(required = false)
		var order: Long? = null,

		@field:Attribute(name = "type", required = false)
		var typeAttr: String? = null,

		var availableLanguages: String? = null,
		var language: String? = null,
		var mimetype: String? = null,
		var size: Long? = null,
		var title: String? = null,
		var type: String? = null,
		var uri: String? = null,
		var url: String? = null
)