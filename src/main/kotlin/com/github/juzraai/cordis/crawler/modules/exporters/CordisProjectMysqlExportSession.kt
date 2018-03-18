package com.github.juzraai.cordis.crawler.modules.exporters

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.model.cordis.*
import com.github.juzraai.cordis.crawler.util.*
import mu.*
import java.util.concurrent.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectMysqlExportSession(private val db: Database, private val cordisProjects: List<CordisProject>) :
		Callable<Unit> {

	companion object : KLogging() {
		private val converter = CordisProjectMySqlRecordConverter()
	}

	private val data = mutableMapOf<String, MutableList<Array<Any?>>>()

	override fun call() {
		cordisProjects.mapNotNull(CordisProject::project).onEach(this::processProject)
		// TODO if -p (passed as a bool config field here, process publications too
		data.forEach { table, records -> db.batchReplace(table, records) }
	}

	private fun addData(table: String, record: Array<Any?>?) {
		if (null != record) fetchTable(table).add(record)
	}

	private fun addData(table: String, records: List<Array<Any?>>) {
		fetchTable(table).addAll(records)
	}

	private fun fetchTable(table: String): MutableList<Array<Any?>> {
		if (!data.containsKey(table)) data[table] = mutableListOf()
		return data[table]!!
	}

	private fun processProject(project: Project) {
		addData("cordis_project", converter.anyToArray(project))
		project.relations?.also { processRelations(project.rcn!!.toString(), "Project", it) }
	}

	private fun processRelations(ownerId: String, ownerType: String, relations: Relations) {
		processRelations("cordis_category", ownerId, ownerType, relations.categories?.filter { null != it.code })
		processRelations("cordis_region", ownerId, ownerType, relations.regions?.filter { null != it.rcn })
		relations.associations?.also {
			processRelations("cordis_call", ownerId, ownerType, it.calls?.filter { null != it.rcn })
			processRelations("cordis_person", ownerId, ownerType, it.persons?.filter { null != it.rcn })
			// TODO ...
		}

		// TODO call processProject for projects, and similar pattern for orgs
	}

	private fun processRelations(table: String, ownerId: String, ownerType: String, records: List<Any>?) {
		if (null != records) {
			addData(table, records.mapNotNull(converter::anyToArray))
			addData("cordis_relation", records.map {
				converter.generateRelationArray(ownerId, ownerType, it)
			})
		}
	}
}