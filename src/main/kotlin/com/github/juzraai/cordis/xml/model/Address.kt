package com.github.juzraai.cordis.xml.model

import org.simpleframework.xml.*

/**
 * @author Zsolt Jurányi
 */
@Default(required = false)
data class Address(
		var city: String? = null,
		var country: String? = null,
		var email: String? = null,
		var faxNumber: String? = null,
		var postalCode: String? = null,
		var postBox: String? = null,
		var street: String? = null,
		var telephoneNumber: String? = null,
		var url: String? = null
)