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

		@field:ElementList(inline = true, entry = "webItem", required = false)
		var webItems: List<WebItem>? = null,

		@field:ElementList(inline = true, entry = "webSite", required = false)
		var webSites: List<WebSite>? = null

		// TODO project, result, webLink
)