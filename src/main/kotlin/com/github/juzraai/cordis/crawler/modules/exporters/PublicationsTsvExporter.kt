package com.github.juzraai.cordis.crawler.modules.exporters

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.model.openaire.sygma.*
import com.github.juzraai.cordis.crawler.modules.*
import mu.*
import java.io.*
import java.text.*
import java.util.*

/**
 * @author Zsolt Jurányi
 */
class PublicationsTsvExporter : ICordisProjectExporter, Closeable {

	companion object : KLogging()

	private data class Column(
			val header: String,
			val valueFunction: Publication.() -> String?
	)

	private var enabled = false
	private var configuration: CordisCrawlerConfiguration? = null
	private var writer: BufferedWriter? = null
	private val columns = listOf(
			Column("doi", { doi }),
			Column("openAireId", { openAireId }),
			Column("dateOfAcceptance", { formatDate(dateOfAcceptance) }),
			Column("type", { publicationType }),
			Column("title", { title }),
			Column("authors", { authors?.joinToString("; ") })
	)

	override fun close() {
		writer?.close()
	}

	override fun exportCordisProjects(cordisProjects: List<CordisProject>) {
		if (!enabled || null == writer) return
		cordisProjects.forEach { cordisProject ->
			cordisProject.publications?.forEach { publication ->
				writeLine(cordisProject.rcn, publication)
			}
		}
	}

	private fun formatDate(date: Date?): String? {
		return date?.run { SimpleDateFormat("yyyy-MM-dd").format(this) }
	}

	override fun initialize(configuration: CordisCrawlerConfiguration, modules: CordisCrawlerModuleRegistry) {
		this.configuration = configuration
		with(configuration) { enabled = tsvExport && (crawlEverything || crawlPublications) }
		if (enabled) {
			val file = outputFile()
			openOutputFile(file)
			logger.info("Publications will be exported to: $file")
			writeHeader()
		}
	}

	private fun openOutputFile(file: File) {
		file.parentFile?.mkdirs()
		writer = BufferedWriter(OutputStreamWriter(FileOutputStream(file), "UTF-8"))
	}

	private fun outputFile(): File {
		val d = SimpleDateFormat("yyyyMMdd-HHmmss").format(configuration?.timestamp)
		return File(configuration!!.outputDirectory, "export${File.separator}$d-publications.tsv")
	}

	private fun writeHeader() {
		val line = "projectRcn\t" + columns.map(Column::header).joinToString("\t")
		writer?.write(line)
		writer?.newLine()
	}

	private fun writeLine(projectRcn: Long, publication: Publication) {
		val line = "$projectRcn\t" + columns.map(Column::valueFunction).joinToString("\t") {
			it.invoke(publication)
					?.replace("\t", "    ")
					?.replace("\n", " ")
					?: ""
		}
		writer?.write(line)
		writer?.newLine()
	}
}