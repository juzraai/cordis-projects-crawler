package com.github.juzraai.cordis.crawler.model.cordis

import org.simpleframework.xml.*

/**
 * @author Zsolt Jurányi
 */
@Default(required = false)
data class WebSite(
		@field:Attribute(name = "type", required = false)
		var type: String? = null,

		var title: String? = null,
		var url: String? = null
)