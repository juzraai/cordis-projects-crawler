package com.github.juzraai.cordis.crawler.modules.exporters

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.util.*
import java.util.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectMysqlExporter : ICordisProjectExporter {

	private var db: Database? = null

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
		// TODO if -p present, ask sessions to export pubs too
		CordisProjectMysqlExportSession(db!!, cordisProjects).call()
	}
}