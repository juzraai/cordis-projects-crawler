package com.github.juzraai.cordis.crawler.util

import org.simpleframework.xml.convert.*
import org.simpleframework.xml.core.*
import java.io.*
import java.util.*

/**
 * @author Zsolt Jurányi
 */
object SimpleXmlParser {

	private val persister = Persister(RegistryStrategy(Registry().apply {
		bind(Date::class.java, DateConverter::class.java)
	}))

	fun <T> parseInputStream(inputStream: InputStream, type: Class<T>): T {
		inputStream.use {
			return persister.read(type, it, false)
		}
	}

	fun <T> parseFile(file: File, type: Class<T>) =
			parseInputStream(file.inputStream(), type)

	fun <T> parseResource(resourceName: String, type: Class<T>) =
			parseInputStream(ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName), type)

	fun <T> parseString(s: String, type: Class<T>) =
			parseInputStream(s.byteInputStream(), type)

}