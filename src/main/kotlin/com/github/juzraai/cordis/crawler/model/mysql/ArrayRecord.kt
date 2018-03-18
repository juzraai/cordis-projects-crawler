package com.github.juzraai.cordis.crawler.model.mysql

import java.util.*

/**
 * @author Zsolt Jurányi
 */
data class ArrayRecord(val array: Array<Any?>) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ArrayRecord) return false
		if (!Arrays.equals(array, other.array)) return false
		return true
	}

	override fun hashCode(): Int {
		return Arrays.hashCode(array)
	}
}