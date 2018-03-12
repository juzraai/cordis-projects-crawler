package com.github.juzraai.cordis.crawler.modules.exporters

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.model.cordis.*
import org.apache.commons.dbcp2.*
import java.sql.*
import java.util.*
import javax.sql.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectMysqlExporter : ICordisProjectExporter {

	var dataSource: DataSource? = null

	override fun initialize(configuration: CordisCrawlerConfiguration) {
		// TODO read config
		dataSource = BasicDataSource().apply {
			url = "jdbc:mysql://localhost/cordis?useUnicode=true&characterEncoding=utf8&useSSL=false"
			username = "root"
			password = "root"
		}

		javaClass.classLoader.getResourceAsStream("mysql_create_tables.sql").use { stream ->
			Scanner(stream).useDelimiter("\\A").use { scanner ->
				executeScript(scanner.next())
			}
		}
	}

	override fun exportCordisProjects(cordisProjects: List<CordisProject>) {
		useConnection { connection ->
			val projectArrays = cordisProjects.mapNotNull(CordisProject::project).map(this::projectToArray)
			batchReplace(connection, "cordis_project", projectArrays)
			// TODO valahogy össze kéne gyújteni a tárolandó objektumokat táblák szerint listába
			// TODO relations-t kitalálni
		}
	}

	private fun batchReplace(connection: Connection, table: String, arrays: List<Array<Any?>>) {
		if (arrays.isEmpty()) return
		val questionMarks = arrays[0].joinToString(",") { "?" }
		val sql = "replace into $table values ($questionMarks)"
		val ps = connection.prepareStatement(sql)
		try {
			arrays.forEach { array ->
				array.forEachIndexed { i, value ->
					ps.setObject(i + 1, value)
				}
				ps.addBatch()
			}
			ps.executeBatch()
		} catch (e: Exception) {
			e.printStackTrace()
			// TODO log
		} finally {
			ps.close()
		}
	}

	private fun executeUpdate(connection: Connection, sql: String): Int {
		val ps = connection.prepareStatement(sql)
		var r = -1
		try {
			r = ps.executeUpdate()
		} catch (e: Exception) {
			e.printStackTrace()
			// TODO log
		} finally {
			ps.close()
		}
		return r
	}

	private fun executeScript(sql: String) {
		useConnection { connection ->
			// this splitting is not elegant, I know,
			// but it's fair enough in our case
			sql.split(";")
					.map(String::trim)
					.filter { it.isNotBlank() }
					.forEach { statement ->
						executeUpdate(connection, statement)
					}
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

	private fun useConnection(block: (Connection) -> Unit) {
		val connection = dataSource?.connection
		try {
			block.invoke(connection!!)
		} catch (e: Exception) {
			e.printStackTrace()
			// TODO log
		} finally {
			connection?.close()
		}
	}
}