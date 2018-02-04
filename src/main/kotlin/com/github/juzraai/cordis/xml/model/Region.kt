package com.github.juzraai.cordis.xml.model

import org.simpleframework.xml.*

/**
 * @author Zsolt Jurányi
 */
@Default(required = false)
data class Region(
		@field:Attribute
		var type: String? = null,

		var euCode: String? = null,
		var isoCode: String? = null,
		var name: String? = null,
		var nutsCode: String? = null,
		var rcn: Long? = null
)