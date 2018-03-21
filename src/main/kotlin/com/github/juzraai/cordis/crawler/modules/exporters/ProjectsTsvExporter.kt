package com.github.juzraai.cordis.crawler.modules.exporters

import com.github.juzraai.cordis.crawler.model.*
import com.github.juzraai.cordis.crawler.model.cordis.*
import mu.*
import java.io.*
import java.text.*
import java.util.*

/**
 * @author Zsolt Jurányi
 */
class ProjectsTsvExporter : ICordisProjectExporter, Closeable {

	companion object : KLogging()

	private data class Column(
			val header: String,
			val valueFunction: Project.() -> String?
	)

	private var configuration: CordisCrawlerConfiguration? = null
	private var enabled = false
	private var writer: BufferedWriter? = null
	private val columns = listOf(
			Column("rcn", { rcn?.toString() }),
			Column("cordisUrl", { "https://www.cordis.europa.eu/project/rcn/${rcn}_en.html" }),
			Column("lastUpdateDate", { formatDate(lastUpdateDate) }),
			Column("reference", Project::reference),
			Column("acronym", Project::acronym),
			Column("status", Project::status),
			Column("frameworkProgramme", {
				relations?.associations?.programmes?.firstOrNull { it.type == "relatedProgramme" }?.frameworkProgramme
			}),
			Column("programme", {
				relations?.associations?.programmes?.firstOrNull { it.type == "relatedProgramme" }?.code
			}),
			Column("subprogramme", {
				relations?.associations?.programmes?.firstOrNull { it.type == "relatedSubProgramme" }?.code
			}),
			Column("title", Project::title),
			Column("startDate", { formatDate(startDate) }),
			Column("endDate", { formatDate(endDate) }),
			Column("website", {
				relations?.associations?.webItems?.firstOrNull { it.typeAttr == "relatedPpmProjectWebsite" }?.url
			}),
			Column("totalCost", { totalCost?.toString() }),
			Column("ecMaxContribution", { ecMaxContribution?.toString() }),
			Column("fundingScheme", {
				relations?.categories?.firstOrNull { it.classification == "projectFundingSchemeCategory" }?.code
			}),
			Column("coordinator", {
				relations?.associations?.organizations?.firstOrNull { it.type == "coordinator" }?.legalName
			}),
			Column("coordinatorCountry", {
				relations?.associations?.organizations?.firstOrNull { it.type == "coordinator" }?.address?.country
			}),
			Column("participants", {
				relations?.associations?.organizations?.filter { it.type == "participant" }
						?.mapNotNull(Organization::legalName)
						?.joinToString("; ")
			}),
			Column("participantCountries", {
				relations?.associations?.organizations?.filter { it.type == "participant" }
						?.mapNotNull { it.address?.country }
						?.toSet()?.sorted()
						?.joinToString(",")
			})
	)

	override fun close() {
		writer?.close()
	}

	override fun exportCordisProjects(cordisProjects: List<CordisProject>) {
		if (!enabled || null == writer) return
		cordisProjects.forEach { cordisProject ->
			cordisProject.project?.also { project ->
				writeLine(project)
			}
		}
	}

	private fun formatDate(date: Date?): String? {
		return date?.run { SimpleDateFormat("yyyy-MM-dd").format(this) }
	}

	override fun initialize(configuration: CordisCrawlerConfiguration) {
		this.configuration = configuration
		enabled = configuration.tsvExport
		if (enabled) {
			val file = outputFile()
			openOutputFile(file)
			logger.info("Projects will be exported to: $file")
			writeHeader()
		}
	}

	private fun openOutputFile(file: File) {
		file.parentFile?.mkdirs()
		writer = BufferedWriter(OutputStreamWriter(FileOutputStream(file), "UTF-8"))
	}

	private fun outputFile(): File {
		val d = SimpleDateFormat("yyyyMMdd-HHmmss").format(configuration?.timestamp)
		return File(configuration!!.outputDirectory, "export${File.separator}$d-projects.tsv")
	}

	private fun writeHeader() {
		val line = columns.map(Column::header).joinToString("\t")
		writer?.write(line)
		writer?.newLine()
	}

	private fun writeLine(project: Project) {
		val line = columns.map(Column::valueFunction).joinToString("\t") {
			it.invoke(project)
					?.replace("\t", "    ")
					?.replace("\n", " ")
					?: ""
		}
		writer?.write(line)
		writer?.newLine()
	}
}