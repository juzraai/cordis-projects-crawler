package com.github.juzraai.cordis.crawler.model.cordis

import org.simpleframework.xml.*

/**
 * @author Zsolt Jurányi
 */
@Root(name = "result")
@Default(required = false)
data class Result(
		@field:Attribute(name = "type", required = false)
		var type: String? = null,

		var rcn: Long? = null
)