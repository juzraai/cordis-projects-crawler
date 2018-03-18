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

	companion object : KLogging() {
		private val converter = CordisProjectMySqlRecordConverter()
	}

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
		db?.batchReplace("cordis_project", projects.mapNotNull(converter::anyToArray))
		projects.onEach {
			if (null != it.rcn && null != it.relations) {
				exportRelations(it.rcn!!.toString(), "Project", it.relations!!)
			}
		}
	}

	private fun exportRelations(ownerId: String, ownerType: String, relations: Relations) {
		val relationArrays = mutableListOf<Array<Any?>>()

		relationArrays.addAll(exportRelations("cordis_call", ownerId, ownerType,
				relations.associations?.calls?.filter { null != it.rcn } ?: listOf()))

		relationArrays.addAll(exportRelations("cordis_category", ownerId, ownerType,
				relations.categories?.filter { null != it.code } ?: listOf()))

		relationArrays.addAll(exportRelations("cordis_region", ownerId, ownerType,
				relations.regions?.filter { null != it.rcn } ?: listOf()))

		db?.batchReplace("cordis_relation", relationArrays)
	}

	private fun exportRelations(table: String, ownerId: String, ownerType: String, records: List<Any>):
			List<Array<Any?>> {
		db?.batchReplace(table, records.mapNotNull(converter::anyToArray))
		return records.map { converter.generateRelationArray(ownerId, ownerType, it) }
	}


}