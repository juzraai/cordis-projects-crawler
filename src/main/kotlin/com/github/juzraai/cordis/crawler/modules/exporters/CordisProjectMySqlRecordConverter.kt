package com.github.juzraai.cordis.crawler.modules.exporters

import com.github.juzraai.cordis.crawler.model.cordis.*
import com.github.juzraai.cordis.crawler.model.mysql.*
import org.apache.commons.codec.digest.*

/**
 * @author Zsolt Jurányi
 */
class CordisProjectMySqlRecordConverter {

	fun anyToArray(any: Any): ArrayRecord? {
		return when (any) {
			is Call -> callToArray(any)
			is Category -> categoryToArray(any)
			is Organization -> organizationToArray(any)
			is Person -> personToArray(any)
			is Programme -> programmeToArray(any)
			is Project -> projectToArray(any)
			is Region -> regiontoArray(any)
			else -> null
		}
	}

	private fun callToArray(call: Call): ArrayRecord {
		with(call) {
			return ArrayRecord(arrayOf(
					rcn,
					identifier,
					title
			))
		}
	}

	private fun categoryToArray(category: Category): ArrayRecord {
		with(category) {
			return ArrayRecord(arrayOf(
					code,
					availableLanguages,
					title
			))
		}
	}

	fun generateRelationArray(ownerId: String, ownerType: String, owned: Any): ArrayRecord {
		val ownedId = getId(owned)
		val ownedType = owned.javaClass.simpleName
		val type = getField(owned, "typeAttr") ?: getField(owned, "type")
		return ArrayRecord(arrayOf(
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
		))
	}

	private fun getId(record: Any?): String? {
		if (null == record) return null
		return getField(record, "rcn")?.toString()
				?: getField(record, "code")?.toString()
				?: hash(record.toString())
	}

	private fun getField(record: Any, field: String) = try {
		val f = record.javaClass.getDeclaredField(field)
		f.isAccessible = true
		f.get(record)
	} catch (e: Exception) {
		// field not available
		null
	}

	private fun hash(s: String) = DigestUtils.sha1Hex(s)

	private fun organizationToArray(organization: Organization): ArrayRecord {
		with(organization) {
			return ArrayRecord(arrayOf(
					rcn,
					address?.city,
					address?.country,
					address?.email,
					address?.faxNumber,
					address?.geolocation,
					address?.postalCode,
					address?.postBox,
					address?.street,
					address?.telephoneNumber,
					address?.url,
					availableLanguages,
					departmentNames?.joinToString("\n", transform = String::trim),
					description,
					id,
					legalName,
					otherDepartmentName,
					shortName,
					vatNumber
			))
		}
	}

	private fun personToArray(person: Person): ArrayRecord {
		with(person) {
			return ArrayRecord(arrayOf(
					rcn,
					address?.city,
					address?.country,
					address?.email,
					address?.faxNumber,
					address?.geolocation,
					address?.postalCode,
					address?.postBox,
					address?.street,
					address?.telephoneNumber,
					address?.url,
					availableLanguages,
					firstName,
					lastName,
					title
			))
		}
	}

	private fun programmeToArray(programme: Programme): ArrayRecord {
		with(programme) {
			return ArrayRecord(arrayOf(
					rcn,
					parent?.rcn,
					availableLanguages,
					code,
					frameworkProgramme,
					pga,
					shortTitle,
					title,
					url
			))
		}
	}

	private fun projectToArray(project: Project): ArrayRecord {
		with(project) {
			return ArrayRecord(arrayOf(
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
			))
		}
	}

	private fun regiontoArray(region: Region): ArrayRecord {
		with(region) {
			return ArrayRecord(arrayOf(
					rcn,
					euCode,
					isoCode,
					name,
					nutsCode
			))
		}
	}
}