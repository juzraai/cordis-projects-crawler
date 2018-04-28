## Building the project

1. Install the freshest [JDK][java] and [Maven][maven]
2. Clone the repository and step into the project directory
3. Run `mvn clean install` in the terminal
4. You can find the JAR file in `target` directory and also in your Maven home
5. And from this point you can include it in your Maven project:

```xml
<dependency>
	<groupId>com.github.juzraai</groupId>
	<artifactId>cordis-projects-crawler</artifactId>
	<version>VERSION</version>
</dependency>
```



## Adding as dependency

Thanks to [JitPack][jitpack], you don't need to clone and build the project to use it as a dependency. Follow the link, click on the green *"Get it"* button next to the latest version and follow the instructions listed there.



## Configuration

Create a `CordisCrawlerConfiguration` object and set its fields via the builder methods. See the [User guide](USER_GUIDE.md) for descriptions of each option.

```kotlin
val configuration = CordisCrawlerConfiguration().seed("...")
	// .crawlEverything()
	// .crawlPublications()
	// .forceDownload()
	// .mysqlExport("user", "host:port", "schema")
	// .outputDirectory("cordis-data")
	// .password("mysql password")
	// .quiet()
	// .tsvExport()
	// .verbose()
```



## Module registry

Tasks inside the crawler are separated into different types of [modules](#modules) (e.g. processors, caches, exporters). They are technically stored in a single list, but they encapsulated in a registry (`CordisCrawlerModuleRegistry`). This way we can easily perform operations on all of the modules.

In order to use the crawler you must create a registry object:

```kotlin
val modules = CordisCrawlerModuleRegistry()
```

The registry provides the following methods:

* `close()`: calls `close()` on each `Closeable` module. This method is called by the crawler at the end of the crawl process.
* `initialize(CordisCrawlerConfiguration)`: calls `initialize` method of each module and passes the configuration and the registry itself. This method is called by the crawler before the crawl process.
* `ofType(Class)`: returns all modules of the given type. This method is called by the crawler and some modules to reach specific modules.



## Calling the crawler

After you created the necessary objects, create the crawler itself too:

```kotlin
val crawler = CordisCrawler(configuration, modules)
```

You can start crawling by calling the `crawlProjects` method of this object. This method has 3 different signatures:

* `crawlProjects()` - uses the configuration passed in crawler constructor
* `crawlProjects(args: Array<String>)` - you can parse command line arguments with this method, it will overwrite the crawler object's internal configuration
* `crawlProjects(seed: Iterator<Long>)` - use this if you want to override only the RCN seed, the internal configuration object will remain untouched

The method does the following:

* calls `modules.initialize()` to initialize [modules](#modules)
* parses [seed](USER_GUIDE.md#seed) string using `ICordisProjectRcnSeed` modules
* iterates through each seed RCN (`Long`)
	* maps it to a `CordisCrawlerRecord` object, this is the record type of the batch processing
	* runs every processor (`ICordisCrawlerRecordProcessor`) module on it
* creates chunks with at most 100 RCNs
	* runs exporter (`ICordisCrawlerRecordExporter`) modules on each
* closes `Closeable` modules with `modules.close()`



## Modules

The crawler consists of many kinds of modules which are used for different tasks. They are defined by interfaces. In some cases a module may call another modules too.



### Main modules

#### ICordisCrawlerModule

This is the root of all ev... ehm... modules :), all modules implement this interface.

**Method:**<br>
Receives the configuration and modules. For Kotlin developers, overriding this method is optional, it has a default no-operation implementation. Java developers must implement the method.

```kotlin
fun initialize(
	configuration: CordisCrawlerConfiguration,
	modules: CordisCrawlerModuleRegistry
) {}
```

**Call:**<br>
All modules of this type will be called at the beginning of the crawl.



#### ICordisProjectRcnSeed

**Method:**<br>
It should parse `configuration.seed` and return an iterator of CORDIS project RCN numbers. If it can't parse the seed string, it should return `null`.

```kotlin
fun projectRcns(): Iterator<Long>?
```

**Call:**<br>
The first module of this type which returns a non-null value will be used.

**Implementations:**<br>
There are a lot of implementations, almost for all [seed options](USER_GUIDE.md#seed). In the order of calling:

* `CordisProjectRcnRangeSeed` - parses RCN range seed
* `CordisProjectRcnListSeed` - parses RCN list or single RCN seed
* `CordisProjectUrlSeed` - parses RCN URL seed
* `AllCordisProjectRcnSeed` - rewrites the seed to a CORDIS search URL which returns all projects
* `CordisProjectSearchUrlSeed` - crawls CORDIS search URL
* `CordisProjectRcnDirectorySeed` - reads RCNs from output directory



#### ICordisCrawlerRecordProcessor

**Method:**<br>
It can make modifications on `CordisCrawlerRecord` record object. If it returns `null`, then the record is filtered out and will not reach further processors or exporters.

```kotlin
fun process(cordisCrawlerRecord: CordisCrawlerRecord): CordisCrawlerRecord?
```

**Call:**<br>
All modules of this type will be called.

**Implementations:**<br>
* `CordisProjectCrawler` - crawls CORDIS project metadata
* `OpenAirePublicationsCrawler` - crawls publication list for the project from OpenAIRE



#### ICordisCrawlerRecordExporter

**Method:**<br>
Receives chunks of `CordisCrawlerRecord` objects, and should export them somewhere.

```kotlin
fun export(cordisCrawlerRecords: List<CordisCrawlerRecord>)
```

**Call:**<br>
All modules of this type will be called after the processing phase.

**Implementations:**<br>
* `MysqlExporter` - exports all data into a MySQL database
* `ProjectsTsvExporter` - exports projects' metadata into a TSV file
* `PublicationsTsvExporter` - exports publications' metadata into a TSV file



### Project XML related modules

They are all used by `CordisProjectCrawler` processor.



#### ICordisProjectXmlReader

**Method:**<br>
Receives an RCN and should return an XML string.

```kotlin
fun projectXmlByRcn(rcn: Long): String?
```

**Call:**<br>
The first module which return a non-null value will be used.

**Implementations:**<br>

* `CordisCrawlerFileCache` - reads from output directory
* `CordisProjectXmlDownloader` - downloads from CORDIS



#### ICordisProjectXmlCache

**Method:**<br>
Receives an XML string and an RCN, and should write down the XML to somewhere (e.g. into a file), from where the module can read it back, because they are also readers.

```kotlin
fun cacheProjectXml(xml: String, rcn: Long)
```

**Call:**<br>
All modules of this type will be called.

**Implementations:**<br>

* `CordisCrawlerFileCache` - writes to output directory



#### ICordisProjectXmlParser

**Method:**<br>
Receives an XML string and should parse it into a `Project` object.

```kotlin
fun parseProjectXml(xml: String): Project?
```

**Call:**<br>
The first module which return a non-null value will be used.

**Implementations:**<br>
* `CordisProjectXmlParser` - uses Simple framework to parse XML



### Publications XML related modules

They are all used by `OpenAirePublicationsCrawler` processor.



#### IOpenAirePublicationsXmlReader

**Method:**<br>
Receives a `Project` object and should return an XML string.

```kotlin
fun publicationsXmlByProject(project: Project): String?
```

**Call:**<br>
The first module which return a non-null value will be used.

**Implementations:**<br>

* `CordisCrawlerFileCache` - reads from output directory
* `OpenAirePublicationsXmlDownloader` - downloads from CORDIS



#### IOpenAirePublicationsXmlCache

**Method:**<br>
Receives an XML string and a `Project` object, and should write down the XML to somewhere (e.g. into a file), from where the module can read it back, because they are also readers.

```kotlin
fun cachePublicationsXml(xml: String, project: Project)
```

**Call:**<br>
All modules of this type will be called.

**Implementations:**<br>

* `CordisCrawlerFileCache` - writes to output directory



#### IOpenAirePublicationsXmlParser

**Method:**<br>
Receive an XML string and should parse it into `List<Publication>`.

```kotlin
fun parsePublicationsXml(xml: String): List<Publication>?
```

**Call:**<br>
The first module which return a non-null value will be used.

**Implementations:**<br>
* `OpenAirePublicationsXmlParser` - uses Simple framework to parse XML



## Extending



### Adding a custom module

Create a module class which implements one of the interfaces listed above, then add its instance to the module registry:

```kotlin
class MyModule : ICordisCrawlerRecordProcessor {
	override fun process(cordisCrawlerRecord: CordisCrawlerRecord): CordisCrawlerRecord? {
		println("Hello World, I'm processing project ${cordisCrawlerRecord.rcn}!")
		return cordisCrawlerRecord
	}
}

fun main(args: Array<String>) {
	val configuration = CordisCrawlerConfiguration()
	var registry = CordisCrawlerModuleRegistry()

	var myModule = MyModule() // instantiating module
	registry.modules.add(myModule) // adding module instance

	CordisCrawler(configuration, registry).crawlProjects(args)
}
```

In some cases (e.g. when implementing seeds, readers or parsers) you may want to add your module with higher priority, to be called before other modules of the same type. You can pass an index as the first argument of `add`:

```kotlin
registry.modules.add(0, myModule) // adding as first module
```

`registry.modules` is a simple `List<ICordisCrawlerModule>`.



### Adding custom configuration

Extend the `CordisCrawlerConfiguration` class and use the intance of your new class to initialize the crawler:

```kotlin
class MyConfiguration : CordisCrawlerConfiguration() {

	@Parameter(names = ["-X", "--extra"], description = "...") // JCommander annotation
	var extraParameter: String? = null
}

fun main(args: Array<String>) {
	val myCconfiguration = MyConfiguration() // using custom class
	// you can still use build methods like .seed("...") and others
	var registry = CordisCrawlerModuleRegistry()
	CordisCrawler(myConfiguration, registry).crawlProjects(args)
}
```

Command line arguments are parsed by [JCommander][jcommander] and your new field will be filled too. You can then use your custom configuration field in your custom modules. Note that inside your custom module, you have to cast the configuration object into `MyConfiguration` to use the extra field.

Run the above program with the following arguments:

```bash
java -jar custom-cordis-crawler.jar -s ... -X ...
```



[java]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[jcommander]: http://jcommander.org/
[jitpack]: https://jitpack.io/#juzraai/cordis-projects-crawler
[kotlin]: https://kotlinlang.org/
[maven]: https://maven.apache.org/