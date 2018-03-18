package com.github.juzraai.cordis.crawler.util

import mu.*
import org.apache.commons.dbcp2.*
import java.sql.*
import javax.sql.*

/**
 * @author Zsolt Jurányi
 */
class Database(host: String, port: Int, schema: String, username: String, password: String) {

	companion object : KLogging()

	var dataSource: DataSource = BasicDataSource().apply {
		url = "jdbc:mysql://$host:$port/$schema?useUnicode=true&characterEncoding=utf8&useSSL=false"
		this.username = username
		this.password = password
	}

	fun batchReplace(table: String, arrays: List<Array<Any?>>) {
		if (arrays.isEmpty()) return
		val questionMarks = arrays[0].joinToString(",") { "?" }
		val sql = "replace into $table values ($questionMarks)"
		useConnection { connection ->
			val ps = connection.prepareStatement(sql)
			try {
				arrays.forEach { array ->
					array.forEachIndexed { i, value ->
						ps.setObject(i + 1, value)
					}
					ps.addBatch()
				}
				logger.trace("Inserting ${arrays.size} records into `$table`")
				ps.executeBatch()
			} catch (e: Exception) {
				logger.error("Failed to insert records into `$table`", e)
			} finally {
				ps.close()
			}
		}
	}

	fun executeUpdate(sql: String): Int {
		var r = 0
		useConnection { r = executeUpdate(it, sql) }
		return r
	}

	private fun executeUpdate(connection: Connection, sql: String): Int {
		val ps = connection.prepareStatement(sql)
		var r = -1
		try {
			r = ps.executeUpdate()
		} catch (e: Exception) {
			logger.error("Failed to run SQL command", e)
		} finally {
			ps.close()
		}
		return r
	}

	fun executeScript(sql: String) {
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

	private fun useConnection(block: (Connection) -> Unit) {
		val connection = dataSource.connection
		try {
			block.invoke(connection!!)
		} catch (e: Exception) {
			logger.error("Failed to perform operations with connection", e)
		} finally {
			connection?.close()
		}
	}
}