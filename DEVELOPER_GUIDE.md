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

Create a `CordisCrawlerConfiguration` object and set its fields. See the [User guide](USER_GUIDE.md) for descriptions of each option.

A `CordisCrawlerModuleRegistry` object is also needed, this provides modules (e.g. processors, caches, exporters) for the crawler. We'll discuss customizing modules later, now it's enough to simply create the object and pass it to the crawler.

Kotlin example:

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

val modules = CordisCrawlerModuleRegistry()

val crawler = CordisCrawler(configuration, modules)
```

Java example:

```java
CordisCrawlerConfiguration configuration = new CordisCrawlerConfiguration().seed("...")
	// .crawlEverything()
	// .crawlPublications()
	// .forceDownload()
	// .mysqlExport("user", "host:port", "schema")
	// .outputDirectory("cordis-data")
	// .password("mysql password")
	// .quiet()
	// .tsvExport()
	// .verbose()
;

CordisCrawlerModuleRegistry modules = new CordisCrawlerModuleRegistry();

CordisCrawler crawler = new CordisCrawler(configuration, modules);
```



## Calling the crawler

After you [configured](#configuration) the crawler, you can start crawling by calling the `crawlProjects` method. This method has 3 different signatures:

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

The crawler consists of many kinds of modules which are used for different tasks and in some cases they may call each other too. The module types are defined by interfaces.



### ICordisCrawlerModule

This is the root of all ev... ehm... modules :), **all modules implement this interface.** It only defines the following method:

```kotlin
fun initialize(
	configuration: CordisCrawlerConfiguration,
	modules: CordisCrawlerModuleRegistry
) {}
```

This method is **called automatically for every module** at the beginning of the crawl, and the actual configuration and module list is passed to the module.

For Kotlin developers, overriding this method is optional, it has a default no-operation implementation. Java users must implement the method.



### ICordisProjectRcnSeed

These modules provide project RCNs for the crawler. They should parse `configuration.seed` string so `initialize` should be implemented as it stores the configuration in a field, to make it available for the interface's method:

```kotlin
fun projectRcns(): Iterator<Long>?
```

If it can't parse the seed string, it should return `null`.

The crawler will call every `ICordisProjectRcnSeed` module each after other, **until one returns a non-null iterator**, and that will be the seed of the crawl process.



### ICordisProjectProcessor

These modules receive a `CordisProject` record object and must return an object of the same type - it's best to return the same object with modifications. If it returns `null`, then that record is filtered out and will not reach further processors or exporters.

```kotlin
fun process(cordisProject: CordisProject): CordisProject?
```

There are two implemented processors: `CordisProjectCrawler` and `OpenAirePublicationsCrawler`.





?> **TODO** list interfaces with description



## Module registry

Modules live in the module registry (`CordisCrawlerModuleRegistry`) which provides the following methods:

?> **TODO** methods, when they called, how to add module, priorities



### Lifecycle

The `crawlProjects` method will do the following:

* calls `modules.initialize()` to initialize modules
* parses seed string using `ICordisProjectRcnSeed` modules ([see above](#ICordisProjectRcnSeed))
* iterates through each seed RCN (`Long`)
	* maps it to a `CordisProject` object, this is the record type of the batch processing
	* runs every processor (`ICordisProjectProcessor`) module on it
* creates chunks with at most 100 RCNs
	* runs exporter (`ICordisProjectExporter`) modules on each
* closes `Closeable` modules with `modules.close()`



### Adding a custom module

?> **TODO** example custom module, example add, example seed, example exporter?, adding custom config class (extending + pass in constructor in main)



[java]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[jitpack]: https://jitpack.io/#juzraai/cordis-projects-crawler
[kotlin]: https://kotlinlang.org/
[maven]: https://maven.apache.org/