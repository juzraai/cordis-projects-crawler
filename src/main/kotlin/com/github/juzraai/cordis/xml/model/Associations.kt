package com.github.juzraai.cordis.xml.model

import org.simpleframework.xml.*

/**
 * @author Zsolt Jurányi
 */
@Default(required = false)
data class Associations(
		@field:ElementList(inline = true, entry = "call", required = false)
		var calls: List<Call>? = null,

		@field:ElementList(inline = true, entry = "organization", required = false)
		var organizations: List<Organization>? = null,

		@field:ElementList(inline = true, entry = "person", required = false)
		var persons: List<Person>? = null,

		@field:ElementList(inline = true, entry = "programme", required = false)
		var programmes: List<Programme>? = null
)