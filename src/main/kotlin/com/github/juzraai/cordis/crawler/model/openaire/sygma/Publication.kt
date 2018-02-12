package com.github.juzraai.cordis.crawler.model.openaire.sygma

import org.simpleframework.xml.*
import java.util.*

/**
 * @author Zsolt Jurányi
 */
@Default(required = false)
data class Publication(

		@field:ElementList(name = "authors", entry = "author", required = false)
		var authors: List<String>? = null,

		@field:Element(name = "bestlicense", required = false)
		var bestLicense: String? = null,

		@field:Element(name = "dateofacceptance", required = false)
		var dateOfAcceptance: Date? = null,

		var description: String? = null,

		var doi: String? = null,

		@field:Element(name = "openaireid", required = false)
		var openAireId: String? = null,

		@field:Element(name = "publicationtype", required = false)
		var publicationType: String? = null,

		@field:ElementList(entry = "sourcejournal", inline = true, required = false)
		var sourceJournals: Set<String>? = null,

		var title: String? = null,

		@field:ElementList(entry = "webresource", inline = true, required = false)
		var webResources: Set<String>? = null
)