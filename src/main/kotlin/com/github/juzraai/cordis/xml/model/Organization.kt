package com.github.juzraai.cordis.xml.model

import org.simpleframework.xml.*

/**
 * @author Zsolt Jurányi
 */
@Default(required = false)
data class Organization(
		@field:Attribute
		var ecContribution: Double? = null,

		@field:Attribute
		var order: Int? = null,

		@field:Attribute
		var terminated: Boolean? = null,

		@field:Attribute
		var type: String? = null,

		@field:ElementList(inline = true, entry = "departmentName", required = false)
		var departmentNames: List<String>? = null,

		var address: Address? = null,
		var availableLanguages: String? = null,
		var id: Long? = null,
		var legalName: String? = null,
		var rcn: Long? = null,
		var relations: Relations? = null,
		var shortName: String? = null,
		var vatNumber: String? = null
)