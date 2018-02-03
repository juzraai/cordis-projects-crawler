package com.github.juzraai.cordis.xml.model

import org.simpleframework.xml.*

/**
 * @author Zsolt Jurányi
 */
@Root(name = "project")
data class Project(
		@field:Element(required = false)
		var language: String? = null
)
