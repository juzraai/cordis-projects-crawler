package com.github.juzraai.cordis.crawler.model.cordis

import org.simpleframework.xml.*

/**
 * @author Zsolt Jurányi
 */
@Default(required = false)
data class Call(
		@field:Attribute
		var type: String? = null,

		var identifier: String? = null,
		var rcn: Long? = null,
		var title: String? = null
)