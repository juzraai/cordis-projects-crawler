package com.github.juzraai.cordis.crawler.modules.exporters

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.model.cordis.*
import com.github.juzraai.cordis.crawler.util.*
import mu.*
import java.util.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectMysqlExporter : ICordisProjectExporter {

	companion object : KLogging()

	var db: Database? = null

	override fun initialize(configuration: CordisCrawlerConfiguration) {
		// TODO read config
		// TODO enable/disable?
		db = Database("localhost", 3306, "cordis", "root", "root")

		javaClass.classLoader.getResourceAsStream("mysql_create_tables.sql").use { stream ->
			Scanner(stream).useDelimiter("\\A").use { scanner ->
				db?.executeScript(scanner.next())
			}
		}
	}

	override fun exportCordisProjects(cordisProjects: List<CordisProject>) {
		// TODO enable/disable

		exportProjects(cordisProjects.mapNotNull(CordisProject::project))
		// TODO if -p, exportPublications

	}

	private fun exportProjects(projects: List<Project>) {
		db?.batchReplace("cordis_project", projects.map(this::projectToArray))
		projects.onEach {
			if (null != it.rcn && null != it.relations) {
				exportRelations(it.rcn!!.toString(), "Project", it.relations!!)
			}
		}
	}

	private fun exportRelations(ownerId: String, ownerType: String, relations: Relations) {
		val relationArrays = mutableListOf<Array<Any?>>()

		val calls = relations.associations?.calls?.filter { null != it.rcn } ?: listOf()
		db?.batchReplace("cordis_call", calls.map(this::callToArray))
		relationArrays.addAll(calls.map { generateRelationArray(ownerId, ownerType, it) })

		val categories = relations.categories?.filter { null != it.code } ?: listOf()
		db?.batchReplace("cordis_category", categories.map(this::categoryToArray))
		relationArrays.addAll(categories.map { generateRelationArray(ownerId, ownerType, it) })

		val regions = relations.regions?.filter { null != it.rcn } ?: listOf()
		//db?.batchReplace("cordis_region", regions.map(this::regionToArray))
		relationArrays.addAll(regions.map { generateRelationArray(ownerId, ownerType, it) })


		db?.batchReplace("cordis_relation", relationArrays)
	}

	private fun generateRelationArray(ownerId: String, ownerType: String, owned: Any): Array<Any?> {
		val ownedId = getId(owned)
		val ownedType = owned.javaClass.simpleName
		val type = getField(owned, "typeAttr") ?: getField(owned, "type")
		return arrayOf(
				"$ownerType/$ownerId-$type-$ownedType/$ownedId",
				ownerId,
				ownerType,
				ownedId,
				ownedType,
				type,
				getField(owned, "classification"),
				getField(owned, "context"),
				getField(owned, "ecContribution"),
				getField(owned, "order"),
				getField(owned, "terminated")
		)
	}

	private fun getId(record: Any?): String? {
		if (null == record) return null
		return getField(record, "rcn")
				?: getField(record, "code")
				?: record.toString() // TODO HASH
	}

	private fun getField(record: Any, field: String) = try {
		val f = record.javaClass.getDeclaredField(field)
		f.isAccessible = true
		f.get(record)?.toString()
	} catch (e: Exception) {
		// field not available
		null
	}

	private fun callToArray(call: Call): Array<Any?> {
		with(call) {
			return arrayOf(
					rcn,
					identifier,
					title
			)
		}
	}

	private fun categoryToArray(category: Category): Array<Any?> {
		with(category) {
			return arrayOf(
					code,
					availableLanguages,
					title
			)
		}
	}

	private fun projectToArray(project: Project): Array<Any?> {
		with(project) {
			return arrayOf(
					rcn,
					acronym,
					availableLanguages,
					contentCreationDate,
					contentUpdateDate,
					contract?.duration,
					contract?.endDate,
					contract?.startDate,
					ecMaxContribution,
					endDate,
					language,
					lastUpdateDate,
					objective,
					reference,
					sourceUpdateDate,
					startDate,
					status,
					statusDetails,
					teaser,
					title,
					totalCost
			)
		}
	}
}