package com.github.juzraai.cordis.crawler.modules.exporters

import com.github.juzraai.cordis.crawler.model.cordis.*
import org.apache.commons.codec.digest.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectMySqlRecordConverter {

	fun anyToArray(any: Any): Array<Any?>? {
		return when (any) {
			is Call -> callToArray(any)
			is Category -> categoryToArray(any)
			is Project -> projectToArray(any)
			is Region -> regiontoArray(any)
			else -> null
		}
	}

	fun callToArray(call: Call): Array<Any?> {
		with(call) {
			return arrayOf(
					rcn,
					identifier,
					title
			)
		}
	}

	fun categoryToArray(category: Category): Array<Any?> {
		with(category) {
			return arrayOf(
					code,
					availableLanguages,
					title
			)
		}
	}

	fun generateRelationArray(ownerId: String, ownerType: String, owned: Any): Array<Any?> {
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

	fun getId(record: Any?): String? {
		if (null == record) return null
		return getField(record, "rcn")
				?: getField(record, "code")
				?: hash(record.toString())
	}

	fun getField(record: Any, field: String) = try {
		val f = record.javaClass.getDeclaredField(field)
		f.isAccessible = true
		f.get(record)?.toString()
	} catch (e: Exception) {
		// field not available
		null
	}

	private fun hash(s: String) = DigestUtils.sha1Hex(s)

	fun projectToArray(project: Project): Array<Any?> {
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

	fun regiontoArray(region: Region): Array<Any?> {
		with(region) {
			return arrayOf(
					rcn,
					euCode,
					isoCode,
					name,
					nutsCode
			)
		}
	}
}