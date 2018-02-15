<h1>Crawler for CORDIS projects</h1>

*Version:* **2.0-SNAPSHOT** &middot; *Author:* Zsolt Jurányi ([web][www], [github][github])

!> **This is a work in progress!** There may be bugs and major code changes can happen any time until I release the final v2.0.

---

## Development progress

**Working:**

* Custom modular batch framework
* Project RCN seeds: single RCN, RCN list, RCN range, project URL
* Download and parse [CORDIS][cordis] project XMLs
* Download and parse publications XML from [OpenAIRE API][oaa] for projects
* Export project metadata into TSV file

**TODO:**

* More seeds: directory, CORDIS search URL, all project
* Exports: MySQL
* Crawl project documents (`webItem`)
* Crawl result XMLs too
* Unified view?



---

## What's new in version 2?

* I rebuilt the crawler from scratch, switched from Java to [Kotlin][kotlin].
* Extracts data from [CORDIS][cordis] XMLs and [OpenAIRE API][oaa] instead of messy HTML pages and encoded JSON strings.
* Can parse information of all projects, from oldest to newest.
* There are new ways to tell the crawler which projects are needed.
* Modular architecture helps developers to extend/alter functionality of the crawler.
* Sexy documentation using [Docsify][docsify]



## How it works

* **The input of the crawler are CORDIS project RCNs.** You can specify one or more, even with a CORDIS URL, see [below](#seed).
* The crawler iterates project RCNs and firstly crawls project data:
	* Tries to read XML from the output directory.
	* If it fails, it downloads the XML from [CORDIS][cordis] into the [output directory](#output-directory).
* Then crawls the publications list:
	* Tries to read XML from the output directory.
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

Seed is the input of the crawler. Seed can be **one or more CORDIS project RCN**, you have a lot of options to specify it:

* Single RCN:  `-s 12345`
* RCN list:    `-s 12345,12347,12350`
* RCN range:   `-s 12345..12350`
* Project URL: `-s https://cordis.europa.eu/project/rcn/12345_en.html`

Full example:

```bash
java -jar cordis-projects-crawler-VERSION.jar -s 12345..12350
```



### Output directory

By default, the crawler will create a folder named `cordis-data` in the working directory, and put downloaded files and exports under it. You can specify another directory if you wish using the `-d` or `--directory` option:

```bash
java -jar cordis-projects-crawler-VERSION.jar -d /path/to/custom/cordis/directory
```

Downloaded files will be placed under `project` and `publications` directory, while exports will be generated into `export` folder inside the output directory.



### TSV export

The crawler can export the selected projects' metadata into a TSV (tabulator separated values) file. TSV files can be easily imported into spreadsheet editor softwares or relational database systems. The first line of the file will be the header. The output file will be named `YYYYMMDD-HHMMSS-projects.tsv` and you can find it under `export` folder inside the output directory. To turn on this export, add `-xt` or `--tsv` to the arguments:

```bash
java -jar cordis-projects-crawler-VERSION.jar -s 12345 -xt
```

Exported fields:

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



### Verbosity

The crawler prints log messages on the screen to inform you what is happening. These log messages contain a timestamp, a level and a message. Log level can be `TRACE`, `DEBUG`, `INFO`, `WARN` or `ERROR`. By default, `TRACE` and `DEBUG` are hidden, because they are useful only when some problem needs investigation, but in other cases they can be disturbing. You can turn them on by adding `-v` or `--verbose` option:

```bash
java -jar cordis-projects-crawler-VERSION.jar -v
```



## Developer documentation



### Using the documentation

You can find this documentation in `docs/README.md`. If you wish to browse the prettier version locally in your browser, please follow the instructions on [Docsify's website][docsify].



### Dependency

Thanks to [JitPack][jitpack], you don't need to clone and build the project to use it as a dependency. Follow the link, click on the green *"Get it"* button next to the latest version and follow the instructions listed there.



### Building the project

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



### Calling the crawler

?> **TODO** config obj, cordis crawler starting


### Modules

?> **TODO** registry, lists, interfaces, initialization, close, cache



### Extending

?> **TODO** extension points: module registry (readers, parsers, etc. lists), example custom processor

?> **TODO** init: setter, close: Closeables will be closed automatically



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