package com.github.juzraai.cordis.crawler.model.openaire.sygma

import org.simpleframework.xml.*

/**
 * @author Zsolt Jurányi
 */
@Root(name = "response")
@Default(required = false)
data class Response(
		var header: Header? = null,
		var publications: List<Publication>? = null
)