<h1>Crawler for CORDIS projects</h1>

*Version:* **2.0-SNAPSHOT** &middot; *Author:* Zsolt Jurányi ([web][www], [github][github])

!> **This is a work in progress!** There may be bugs and major code changes can happen any time until I release the final v2.0.

---

## Development progress

**Working:**

* Custom modular batch framework
* Project RCN seeds: single RCN, RCN list, RCN range, project URL, [CORDIS][cordis] search URL, output directory
* Download and parse [CORDIS][cordis] project XMLs
* Download and parse publications XML from [OpenAIRE API][oaa] for projects
* Export project and publication metadata into [TSV][tsv] file and/or MySQL database

**TODO:**

* Finalize code
* Finish documentation



---

## What's new in version 2?

* I rebuilt the crawler from scratch, switched from Java to [Kotlin][kotlin].
* Extracts data from [CORDIS][cordis] XMLs and [OpenAIRE API][oaa] instead of messy HTML pages and encoded JSON strings.
* Can parse information of all projects, from oldest to newest.
* There are [new ways to tell](#seed) the crawler which projects are needed.
* [Modular architecture](#modules) helps developers to extend/alter functionality of the crawler.
* Sexy documentation using [Docsify][docsify]



## Future plans, ideas

* Crawl project documents (`webItem`) with option `-d`
* Crawl result XMLs with option `-r`
* Generate "unified view" export (with option `-u` maybe)



## How it works

* **The input of the crawler are [CORDIS][cordis] project RCNs.** You can specify one or more, even with a [CORDIS][cordis] URL, see [below](#seed).
* The crawler iterates project RCNs and firstly crawls project data:
	* Tries to read XML from the [output directory](#output-directory).
	* If it fails, it downloads the XML from [CORDIS][cordis] into the [output directory](#output-directory).
* Then crawls the publications list:
	* Tries to read XML from the [output directory](#output-directory).
	* If it fails, it downloads the XML from [OpenAIRE][oaa] into the [output directory](#output-directory).
* Runs exporter modules to generate exports from project information.



## Running the crawler

1. Install the freshest [JRE][java]
2. Download the [latest release][release]
3. You can run the JAR file as follows from the terminal:

```bash
java -jar cordis-projects-crawler-VERSION.jar [arguments]
```

If you run it without arguments, the program will print out the available options. Let's walk through them here.



### Seed

Seed is the input of the crawler, it can be **one or more [CORDIS][cordis] project RCN**. You can specify it with `-s`:

```bash
java -jar cordis-projects-crawler-VERSION.jar -s <seed>
```



#### Fetching a single project

You can specify a single project RCN in the following ways. You can use the RCN itself or one of the [CORDIS][cordis] links for that project.

```
-s 12345
-s https://cordis.europa.eu/project/rcn/12345_en.html
-s https://cordis.europa.eu/project/rcn/12345_en.xml
```



#### Fetching multiple projects

You can specify an RCN range. The lower and upper numbers are both inclusive, and the separator is a two dots (`..`), without spaces.

```
-s 12345..12350
```

You can also specify a list of RCNs by using comma (`,`) as separator. For example, the range seed above is equivalent to this list seed:

```
-s 12345,12346,12347,12348,12349,12350
```



#### Fetching CORDIS search results

If you need to crawl all projects returned by your search query on [CORDIS][cordis], you can specify result list URL to the crawler. The first one is produced by the big search box or the *Advanced search* feature, and the second one is from *Search projects and results* box under *Projects & Results*:

```
-s https://cordis.europa.eu/search/result_en?q=something
-s https://cordis.europa.eu/projects/result_en?q=%27something%27
```

The crawler will run through the result list pages and extract project RCNs.



#### Fetching all projects

You can also tell the crawler to fetch all available projects on [CORDIS][cordis]:

```
-s all
```

Actually, this is a shorthand for this search URL seed:

```
-s https://cordis.europa.eu/projects/result_en?q=contenttype%3D%27project%27
```



#### Reprocessing projects

You may need to generate another export, or refresh the files you downloaded previously. Then you can tell the crawler to read project RCNs from the [output directory](#output-directory):

```
-s dir
```

The program will look for RCN directories like `project/012345/` inside the [output directory](#output-directory).



### Forcing re-download

The crawler tries to read the required files from the [output directory](#output-directory), and only downloads data from the servers if it can't succeed. However, there may be cases when you need to refresh the files. You can pass `-f` or `--force-download` option to skip cache reading before downloads:

```bash
java -jar cordis-projects-crawler-VERSION.jar -s 12345 -f
```



### Output directory

By default, the crawler will create a folder named `cordis-data` in the working directory, and put downloaded files and exports under it. You can specify another directory if you wish using the `-o` or `--output-dir` option:

```bash
java -jar cordis-projects-crawler-VERSION.jar -s ... -o /path/to/custom/cordis/directory
```

The output directory will contain a `project` and an `export` directory for downloaded and generated files. Files of each project will be inside the project RCN directory. Sample structure:

```
cordis-data/
	export/
		20180131-2359-projects.tsv
		20180131-2359-publications.tsv
	project/
		012345/
			012345-project.xml.gz
			012345-publications.xml.gz
```



### What data to crawl

The program will always crawl project metadata from [CORDIS][cordis]. The crawler can additionally fetch publications' information for each project using [OpenAIRE API][oaa]. If you need this, pass `-p` or `--crawl-publications` argument.

```bash
java -jar cordis-projects-crawler-VERSION.jar -s 12345 -p
```

Alternatively, you can pass `-e` or `--crawl-everything` to crawl all available project related information. In the current version of the program, this option equivalent to `-p`.

```bash
java -jar cordis-projects-crawler-VERSION.jar -s 12345 -e
```



### TSV export

The crawler can export the selected projects' metadata into [TSV][tsv] (tabulator separated values) files. They can be easily imported into spreadsheet editor softwares or relational database systems. The first line of each file will be its header. The following output files will be generated under `export` folder inside the [output directory](#output-directory):

* `YYYYMMDD-HHMMSS-projects.tsv` - project metadata
* `YYYYMMDD-HHMMSS-publications.tsv` - publication metadata

To turn on these exports, add `-t` or `--tsv-export` to the arguments:

```bash
java -jar cordis-projects-crawler-VERSION.jar -s 12345 -t
```

Exported project fields:

* rcn
* cordisUrl
* lastUpdateDate
* reference
* acronym
* status
* frameworkProgramme
* programme
* subprogramme
* title
* startDate
* endDate
* website
* totalCost
* ecMaxContribution
* fundingScheme
* coordinator
* coordinatorCountry
* participants
* participantCountries

Exported publication fields:

* projectRcn
* doi
* openAireId
* dateOfAcceptance
* type
* title
* authors



### MySQL export

Firstly, you need to create a database using your MySQL client:

```sql
CREATE SCHEMA `your_database` DEFAULT CHARACTER SET utf8_general_ci;
```

Then pass the database connection parameters using `-m` (or `--mysql-export`) and `-P` as follows:

```bash
java -jar cordis-projects-crawler-VERSION.jar -m user@host:port/schema -P password
```

You can omit `:port` if the port is `3306` and you can omit `host` if it's `localhost` in your environment, but make sure you specify at least `user@/schema`.

?> **TODO** schema - a pic maybe?



### Console output

The crawler prints log messages on the screen to inform you what is happening. These log messages contain a timestamp, a level and a message. Log level can be `TRACE`, `DEBUG`, `INFO`, `WARN` or `ERROR`. By default, `TRACE` and `DEBUG` are hidden, because they are useful only when some problem needs investigation, but in other cases they can be disturbing. You can turn them on by adding `-v` or `--verbose` option:

```bash
java -jar cordis-projects-crawler-VERSION.jar -v
```

If you are not interested in any of the log messages and you need no console output at all, you can use the `-q` or `--quiet` option:

```bash
java -jar cordis-projects-crawler-VERSION.jar -q
```

Note that this option will not have any effect if you turn on verbose mode.



## Aggressivity, networking

To reduce harm on [CORDIS][cordis] and [OpenAIRE][oaa] servers, the crawler **waits at least 2 seconds before sending another request to the same server**. This does not mean that the program sleeps 2 seconds everytime. After the processing of the previous request, the crawler calculates the required sleep time to ensure a delay of at least 2 seconds. If the processing took more time, then there will be no sleep.

When downloading result lists, the crawler requests the **maximum amount of results per request.** On [CORDIS][cordis], it's 100, on [OpenAIRE][oaa] it's 10&nbsp;000 items. This reduces the number of requests needed to crawl a list. In case of [OpenAIRE][oaa], only one request is sent per project, because I assume that  a project cannot have more than 10K publications.

[CORDIS][cordis] search results are requested in CSV format to reduce bandwith usage.

If there's an HTTP error, or an invalid response is received, the crawler increases the delay and retries the request.



## Crawl time

Especially when crawling a search result list or a large range of RCNs, you may want to estimate the time needed for the crawl. The main components of crawl time are the request delays and the transfer times of each request. Processing itself doesn't need significant amount of time.

There are 2 requests per project, one for the [CORDIS][cordis] XML and one for the [OpenAIRE][oaa] publication list. The 2 second delay should be counted only one time, because the 2 requests are sent to different servers.

```
N project by RCN = N * (2 second delay + 2 * request time)
```

So if you are crawling 1000 projects, and a request takes 1 second, fetching project data takes about an hour.

If you crawl a search result list, there are list pages per every 100 project (delay + request time). Those 1000 projects need 10 list pages, and if a list page needs 2 seconds, fetching these takes only ~40 seconds in addition, so they are not so significant.

Please note that request time depends on both your internet connection and the speed/health of the servers.



## Building the project

1. Install the freshest [JDK][java] and [Maven][maven]
2. Clone the repository and step into the project directory
3. Run `mvn clean install` in the terminal
4. You can find the JAR file in `target` directory and also in your [Maven][maven] home
5. And from this point you can include it in your [Maven][maven] project:

```xml
<dependency>
	<groupId>com.github.juzraai</groupId>
	<artifactId>cordis-projects-crawler</artifactId>
	<version>VERSION</version>
</dependency>
```



## Using the crawler in your project

### Adding as dependency

Thanks to [JitPack][jitpack], you don't need to clone and build the project to use it as a dependency. Follow the link, click on the green *"Get it"* button next to the latest version and follow the instructions listed there.



### Configuration

Create a `CordisCrawlerConfiguration` object and set its fields. See [Running the crawler](#running-the-crawler) chapter for the descriptions of each option.

A `CordisCrawlerModuleRegistry` object is also needed, this provides modules (e.g. processors, caches, exporters) for the crawler. We'll discuss customizing modules later, now it's enough to simply create the object and pass it to the crawler.

Kotlin example:

```kotlin
val configuration = CordisCrawlerConfiguration(
	// crawlEverything = true,
	// crawlPublications = true,
	// forceDownload = true,
	// mysqlExport = "user@host:port/schema",
	// outputDirectory = "cordis-data",
	// password = "mysql password",
	// quiet = true,
	seed = "...",
	// tsvExport = true,
	// verbose = true
)

val modules = CordisCrawlerModuleRegistry()

val crawler = CordisCrawler(configuration, modules)
```

Java example:

```java
CordisCrawlerConfiguration configuration = new CordisCrawlerConfiguration();
// configuration.setCrawlEverything(true);
// configuration.setCrawlPublications(true);
// configuration.setForceDownload(true);
// configuration.setMysqlExport("user@host:port/schema");
// configuration.setOutputDirectory("cordis-data");
// configuration.setPassword("mysql password");
// configuration.setQuiet(true);
configuration.setSeed("...");
// configuration.setTsvExport(true);
// configuration.setVerbose(true);

CordisCrawlerModuleRegistry modules = new CordisCrawlerModuleRegistry();

CordisCrawler crawler = new CordisCrawler(configuration, modules);
```



### Calling the crawler

You can start the crawling by calling the `crawlProjects` method of the `CordisCrawler` object. This method has 3 different signatures:

* `crawlProjects()` - uses the configuration passed in crawler constructor
* `crawlProjects(args: Array<String>)` - you can pass command line arguments here to override the crawler object's internal configuration
* `crawlProjects(seed: Iterator<Long>)` - use this if you want to override only the RCN seed

The method doesn't return anything just does the following:

* initializes modules
* iterates through seed RCNs and runs processors on each RCN
* finalizes modules



## Modules

### Interfaces

?> **TODO** list interfaces with description

### Lifecycle

?> **TODO** init all, call of processors, chunking, call of exporters, close closeables

### Registry

?> **TODO** methods, when they called, how to add module, priorities

### Adding a custom module

?> **TODO** example custom module, example add



## Hosting the documentation

You can find this documentation in `docs/README.md`. If you wish to browse the prettier version locally in your browser, please follow the instructions on [Docsify's website][docsify].



[jitpack]: https://jitpack.io/#juzraai/cordis-projects-crawler
[release]: https://github.com/juzraai/cordis-projects-crawler/releases/latest

[github]: http://github.com/juzraai
[www]: http://juzraai.github.io/

[cordis]: https://cordis.europa.eu/
[docsify]: https://docsify.js.org/#/quickstart
[java]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[kotlin]: https://kotlinlang.org/
[maven]: https://maven.apache.org/
[oaa]: http://api.openaire.eu/
[tsv]: https://en.wikipedia.org/wiki/Tab-separated_values