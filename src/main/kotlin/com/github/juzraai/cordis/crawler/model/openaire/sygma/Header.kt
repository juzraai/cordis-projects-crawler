package com.github.juzraai.cordis.crawler.model.openaire.sygma

import org.simpleframework.xml.*

/**
 * @author Zsolt Jurányi
 */
@Default(required = false)
data class Header(
		var page: Int? = null,
		var size: Int? = null,
		var total: Int? = null
)