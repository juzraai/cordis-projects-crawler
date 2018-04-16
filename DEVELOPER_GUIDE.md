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

Tasks inside the crawler are separated into different types of [modules](#modules) (e.g. processors, caches, exporters). They are technically stored in a single list, but they encapsulated in a registry (`CordisCrawlerModuleRegistry`). This way we can easily perform operations on all of the modules:

?> **TODO** methods, when they called, priorities

In order to use the crawler you must create a registry object:

```kotlin
val modules = CordisCrawlerModuleRegistry()
```



## Calling the crawler

After you created the necessary object, create the crawler itself too:

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
	* maps it to a `CordisProject` object, this is the record type of the batch processing
	* runs every processor (`ICordisProjectProcessor`) module on it
* creates chunks with at most 100 RCNs
	* runs exporter (`ICordisProjectExporter`) modules on each
* closes `Closeable` modules with `modules.close()`



## Modules

The crawler consists of many kinds of modules which are used for different tasks. They are defined by interfaces. In some cases a module may call another modules too.



### Main modules

`ICordisCrawlerModule` is the root of all ev... ehm... modules :), **all modules implement this interface.** It only defines a method which receives the configuraiton and the modules. This method is **called automatically for every module** at the beginning of the crawl, and the actual configuration and module list is passed to the module. For Kotlin developers, overriding this method is optional, it has a default no-operation implementation. Java users must implement the method.

```kotlin
fun initialize(
	configuration: CordisCrawlerConfiguration,
	modules: CordisCrawlerModuleRegistry
) {}
```

`ICordisProjectRcnSeed` modules provide project RCNs for the crawler. They should parse `configuration.seed` string so it should store the configuration in the `initialize` method. If it can't parse the seed string, it should return `null`. **The first module which return a non-null value will be used.** There are a lot of implementations, almost for all [seed options](USER_GUIDE.md#seed).

```kotlin
fun projectRcns(): Iterator<Long>?
```

`ICordisProjectProcessor` modules receive a `CordisProject` record object and must return an object of the same type - it's best to return the same object with modifications. If it returns `null`, then that record is filtered out and will not reach further processors or exporters. There are 2 implementations: `CordisProjectCrawler` and `OpenAirePublicationsCrawler`.

```kotlin
fun process(cordisProject: CordisProject): CordisProject?
```

`ICordisProjectExporter` modules receive chunks (lists) of `CordisProject` objects, and they should export them somewhere. **All exporters will be called** after the processing phase by the crawler.

```kotlin
fun exportCordisProjects(cordisProjects: List<CordisProject>)
```



### Project XML related modules

They are all used by `CordisProjectCrawler` processor.

`ICordisProjectXmlReader` modules receive an RCN and should return an XML string. **The first module which return a non-null value will be used.**

```kotlin
fun projectXmlByRcn(rcn: Long): String?
```

`ICordisProjectXmlCache` modules receive an XML string and an RCN, and they should write down the XML to somewhere (e.g. into a file), from where they can read it back, because they are also readers. The processor **calls every module of this type.**

```kotlin
fun cacheProjectXml(xml: String, rcn: Long)
```

`ICordisProjectXmlParser` modules receive an XML string and they should parse it into a `Project` object. **The first module which return a non-null value will be used.**

```kotlin
fun parseProjectXml(xml: String): Project?
```



### Publications XML related modules

They are all used by `OpenAirePublicationsCrawler` processor. Their naming conventions and functions are similar to project XML related modules.





?> **TODO** list interfaces with description







### Adding a custom module

?> **TODO** example custom module, example add, example seed, example exporter?, adding custom config class (extending + pass in constructor in main)



[java]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[jitpack]: https://jitpack.io/#juzraai/cordis-projects-crawler
[kotlin]: https://kotlinlang.org/
[maven]: https://maven.apache.org/