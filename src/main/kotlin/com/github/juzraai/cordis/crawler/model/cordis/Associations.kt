package com.github.juzraai.cordis.crawler.model.cordis

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
		var programmes: List<Programme>? = null,

		@field:ElementList(inline = true, entry = "project", required = false)
		var projects: List<Project>? = null,

		@field:ElementList(inline = true, entry = "result", required = false)
		var results: List<Result>? = null,

		@field:ElementListUnion(
				ElementList(inline = true, entry = "webItem", required = false),
				ElementList(inline = true, entry = "webLink", required = false),
				ElementList(inline = true, entry = "webSite", required = false)
		)
		var webItems: List<WebItem>? = null
)