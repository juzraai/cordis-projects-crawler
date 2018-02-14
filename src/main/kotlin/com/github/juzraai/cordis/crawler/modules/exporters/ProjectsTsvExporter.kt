package com.github.juzraai.cordis.crawler.modules.exporters

import com.github.juzraai.cordis.crawler.model.*
import mu.*
import java.io.*
import java.text.*
import java.util.*

/**
 * @author Zsolt Jurányi
 */
class ProjectsTsvExporter : ICordisProjectExporter,
		Closeable {

	companion object : KLogging()

	private var enabled = false
	private val columns = mutableListOf<Pair<String, (CordisProject) -> String>>()
	private var writer: BufferedWriter? = null

	private var _configuration: CordisCrawlerConfiguration? = null
	override var configuration: CordisCrawlerConfiguration?
		get() = _configuration
		set(value) {
			_configuration = value
			enabled = _configuration?.tsv ?: false
			if (enabled) {
				val file = outputFile()
				openOutputFile(file)
				buildColumnModel()
				logger.info("Projects will be exported to: $file")
				writeHeader()
			}
		}

	private fun buildColumnModel() {
		// CORDIS dataset header:
		// rcn;id;acronym;status;programme;topics;frameworkProgramme;title;startDate;endDate;projectUrl;objective;totalCost;ecMaxContribution;call;fundingScheme;coordinator;coordinatorCountry;participants;participantCountries;subjects

		// v1.x export header:
		// Name;Title;Website;Coordinator's country;Publications;From;To;Status;Contract type;Cost;Cost curr.;EU contribution;EU contrib. curr.;Programme acronym;Subprogramme area;Record number (RCN);Project reference;Last updated on;On CORDIS;

		columns.apply {
			add(Pair("rcn", { it -> it.rcn.toString() }))
			add(Pair("reference", { it -> it.project?.reference ?: "" }))
			// TODO add more columns
		}
	}

	override fun close() {
		writer?.close()
	}

	override fun exportCordisProjects(cordisProjects: List<CordisProject>) {
		if (!enabled || null == writer) return
		cordisProjects.forEach { project ->
			val line = columns.joinToString("\t") { it.second.invoke(project) }
			writer?.write(line)
			writer?.newLine()
		}
	}

	private fun openOutputFile(file: File) {
		file.parentFile?.mkdirs()
		writer = BufferedWriter(OutputStreamWriter(FileOutputStream(file), "UTF-8"))
	}

	private fun outputFile(): File {
		val d = SimpleDateFormat("yyyyMMdd-HHmmss").format(Date())
		return File(configuration!!.directory, "export${File.separator}$d-projects.csv")
	}

	private fun writeHeader() {
		if (!enabled || null == writer) return
		val line = columns.joinToString("\t") { it.first }
		writer?.write(line)
		writer?.newLine()
	}
}