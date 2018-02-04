package com.github.juzraai.cordis.xml.model

/**
 * @author Zsolt Jurányi
 */
data class Relations(
		// TODO associations
		// TODO note for mysql export:
		// XML attributes of related entities (e.g. organization.ecContribution) are props of the relation, not the entity!
		var categories: MutableList<Category> = mutableListOf()
)