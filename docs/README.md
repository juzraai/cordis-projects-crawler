<h1>Crawler for CORDIS projects</h1>

*Version:* **2.0-SNAPSHOT** &middot; *Author:* Zsolt Jurányi ([web][www], [github][github])

!> **This is a work in progress!** There may be bugs and major code changes can happen any time until I release the final v2.0.

---

## Features

**Already working:**

* Download [CORDIS][cordis] project XMLs into a directory
* Can download a single RCN or a range of RCNs
* Can parse some fields from project XML (available to developers)


**Planned features:**

* Read RCNs from output directory
* Crawl all RCNs available on CORDIS
* Parse all fields from project XMLs
* Handle result XMLs too
* Recursive crawl (project in seed, crawl related results too)
* CSV export
* MySQL export


## Usage

1. Install the freshest [JRE][java]
2. Download the [latest release][release]
3. You can run the JAR file as follows from the terminal:

```bash
java -jar cordis-projects-crawler-VERSION.jar [arguments]
```

If you run it without further arguments, the program will print out the available options.



### How it works

?> **TODO** seed, fetch, cache, parse, export



### Use cases

?> **TODO** one RCN, RCN range, exports



## Developer documentation



### Using the documentation

You can find this documentation in `docs/README.md`. If you wish to browse the prettier version locally in your browser, please follow the instructions on [Docsify's website][docsify].



### Building the project

1. Install the freshest [JDK][java] and [Maven][maven]
2. Step into the project directory
3. Run `mvn clean install` in the terminal
4. You can find the JAR file in `target` directory
5. And from this point you can include it in your Maven project:

```xml
<dependency>
	<groupId>com.github.juzraai</groupId>
	<artifactId>cordis-projects-crawler</artifactId>
	<version>VERSION</version>
</dependency>
```



### Overview

?> **TODO** batch states like above + custom processor

?> **TODO** extension points (readers, parsers, etc. lists), custom processor 



[github]: http://github.com/juzraai
[release]: https://github.com/juzraai/cordis-projects-crawler/releases/latest
[www]: http://juzraai.github.io/

[cordis]: https://cordis.europa.eu/
[docsify]: https://docsify.js.org/#/quickstart
[java]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[maven]: https://maven.apache.org/