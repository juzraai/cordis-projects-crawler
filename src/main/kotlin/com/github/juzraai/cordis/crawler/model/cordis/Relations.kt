package com.github.juzraai.cordis.crawler.model.cordis

import org.simpleframework.xml.*

/**
 * @author Zsolt Jurányi
 */
@Default(required = false)
data class Relations(
		// XML attributes of related entities (e.g. organization.ecContribution)
		// are props of the relation, not the entity!

		var associations: Associations? = null,
		var categories: List<Category>? = null,
		var regions: List<Region>? = null // = mutableListOf()
)