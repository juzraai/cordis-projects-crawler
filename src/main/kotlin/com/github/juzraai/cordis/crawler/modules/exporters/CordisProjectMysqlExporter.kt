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
				exportRelations(it.rcn!!, "project", it.relations!!)
			}
		}
	}

	private fun exportRelations(ownerId: Long, ownerType: String, relations: Relations) {
		val relationArrays = mutableListOf<Array<Any?>>()

		// TODO kéne valami generikusabb megoldás a relation előállításra
		// Any-ből kiszedhetné az ID-t: rcn, code mező; vagy toString SHA-1 hash-e
		// a type-ot: typeAttr, vagy ha nincs olyan mező, akkor type
		// és a többit

		val categoryArrays = mutableListOf<Array<Any?>>()
		relations.categories?.filter { null != it.code }?.forEach { category ->
			//categoryArrays.add(categoryToArray(category))
			relationArrays.add(relationToArray(
					ownerId, ownerType,
					category.code!!, "category",
					category.type, category.classification
			))
		}
		db?.batchReplace("cordis_category", categoryArrays)

		val regionArrays = mutableListOf<Array<Any?>>()
		relations.regions?.filter { null != it.rcn }?.forEach { region ->
			//regionArrays.add(regionToArray(region))
			relationArrays.add(relationToArray(
					ownerId, ownerType,
					region.rcn!!.toString(), "region",
					region.type))
		}
		db?.batchReplace("cordis_region", regionArrays)

		db?.batchReplace("cordis_relation", relationArrays)
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

	private fun relationToArray(
			ownerId: Long, ownerType: String, ownedId: String, ownedType: String, type: String?,
			classification: String? = null, context: Boolean? = null, ecContribution: Double? = null,
			order: Int? = null, terminated: Boolean? = null): Array<Any?> {
		return arrayOf(
				"$ownerType/$ownerId-$type-$ownedType/$ownedId",
				ownerId,
				ownerType,
				ownedId,
				ownedType,
				type,
				classification,
				context,
				ecContribution,
				order,
				terminated
		)
	}


}