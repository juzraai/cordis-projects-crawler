package com.github.juzraai.cordis.xml.model

import org.simpleframework.xml.*
import java.util.*

/**
 * @author Zsolt Jurányi
 */
@Root(name = "project")
@Default(required = false)
data class Project(
		var acronym: String? = null,
		var availableLanguages: String? = null,
		var contentCreationDate: Date? = null,
		var contentUpdateDate: Date? = null,
		var contract: Contract? = null,
		var ecMaxContribution: Double? = null,
		var endDate: Date? = null,
		var language: String? = null,
		var lastUpdateDate: Date? = null,
		var objective: String? = null,
		var rcn: Long? = null,
		var reference: Long? = null,
		var sourceUpdateDate: Date? = null,
		var startDate: Date? = null,
		var status: String? = null,
		var teaser: String? = null,
		var title: String? = null,
		var totalCost: Double? = null
		// TODO ProjectRelations
)
