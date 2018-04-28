## How it works?

* **The input of the crawler is a list of [CORDIS][cordis] project RCNs.** You can [specify one or more](#seed), even with a CORDIS URL.
* The crawler iterates project RCNs and firstly crawls project data:
	* Tries to read XML from the [output directory](#output-directory).
	* If it fails, it downloads the XML from CORDIS into the output directory.
* Then, [if required](#what-data-to-crawl) crawls the publications list:
	* Tries to read XML from the output directory.
	* If it fails, it downloads the XML from [OpenAIRE][oaa] into the output directory.
* Runs exporter modules to generate exports from project information.



## Running the crawler

1. Install the freshest [JRE][java]
2. Download the [latest release][release]
3. You can run the JAR file as follows from the terminal:

```bash
java -jar cordis-projects-crawler-VERSION-standalone.jar [arguments]
```

If you run it without arguments, the program will print out the available options, but we'll walk through them here.



## Seed

Seed is the input of the crawler, it can be **one or more CORDIS project RCN**. You can specify it with `-s`:

```bash
java -jar cordis-projects-crawler-VERSION-standalone.jar -s <seed>
```



### Fetching a single project

You can specify a single project RCN in the following ways. You can use the RCN itself or one of the CORDIS links for that project.

```
-s 12345
-s https://cordis.europa.eu/project/rcn/12345_en.html
-s https://cordis.europa.eu/project/rcn/12345_en.xml
```



### Fetching multiple projects

You can specify an RCN range. The lower and upper numbers are both inclusive, and the separator is a two dots (`..`), without spaces.

```
-s 12345..12350
```

You can also specify a list of RCNs by using comma (`,`) as separator. For example, the range seed above is equivalent to this list seed:

```
-s 12345,12346,12347,12348,12349,12350
```



### Fetching CORDIS search results

If you need to crawl all projects returned by your search query on CORDIS, you can specify result list URL to the crawler. The first one is produced by the big search box or the *Advanced search* feature, and the second one is from *Search projects and results* box under *Projects & Results*:

```
-s https://cordis.europa.eu/search/result_en?q=something
-s https://cordis.europa.eu/projects/result_en?q=%27something%27
```

The crawler will run through the result list pages and extract project RCNs.



### Fetching all projects

You can also tell the crawler to fetch all available projects on CORDIS:

```
-s all
```

Actually, this is a shorthand for this search URL seed:

```
-s https://cordis.europa.eu/projects/result_en?q=contenttype%3D%27project%27
```



### Reprocessing projects

You may need to generate another export, or refresh the files you downloaded previously. Then you can tell the crawler to read project RCNs from the [output directory](#output-directory):

```
-s dir
```

The program will look for RCN directories like `project/012345/` inside the output directory.



## Forcing re-download

The crawler tries to read the required files from the output directory, and only downloads data from the servers if it can't succeed. However, there may be cases when you need to refresh the files. You can pass `-f` or `--force-download` option to skip cache reading before downloads:

```bash
java -jar cordis-projects-crawler-VERSION-standalone.jar -s ... -f
```



## Output directory

By default, the crawler will create a folder named `cordis-data` in the working directory, and put downloaded files and exports under it. You can specify another directory if you wish using the `-o` or `--output-dir` option:

```bash
java -jar cordis-projects-crawler-VERSION-standalone.jar -s ... -o /path/to/custom/cordis/directory
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



## What data to crawl

The program will always crawl project metadata from [CORDIS][cordis]. The crawler can additionally fetch publications' information for each project using [OpenAIRE API][oaa]. If you need this, pass `-p` or `--crawl-publications` argument.

```bash
java -jar cordis-projects-crawler-VERSION-standalone.jar -s ... -p
```

Alternatively, you can pass `-e` or `--crawl-everything` to crawl all available project related information. In the current version of the program, this option equivalent to `-p`.

```bash
java -jar cordis-projects-crawler-VERSION-standalone.jar -s ... -e
```



## TSV export

The crawler can export the selected projects' metadata into [TSV][tsv] (tabulator separated values) files. They can be easily imported into spreadsheet editor softwares or relational database systems. The first line of each file will be its header. The following output files will be generated under `export` folder inside the output directory:

* `YYYYMMDD-HHMMSS-projects.tsv` - project metadata
* `YYYYMMDD-HHMMSS-publications.tsv` - publication metadata

To turn on these exports, add `-t` or `--tsv-export` to the arguments:

```bash
java -jar cordis-projects-crawler-VERSION-standalone.jar -s ... -t
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



## MySQL export

Firstly, you need to create a database using your MySQL client:

```sql
CREATE SCHEMA `your_database` DEFAULT CHARACTER SET utf8_general_ci;
```

Then pass the database connection parameters using `-m` (or `--mysql-export`) and `-P` as follows:

```bash
java -jar cordis-projects-crawler-VERSION-standalone.jar -s ... -m user@host:port/schema -P password
```

You can omit `:port` if the port is `3306` and you can omit `host` if it's `localhost` in your environment, but make sure you specify at least `user@/schema`.

The following picture shows the tables and columns of the database model:

![MySQL Model](schema/mysql-model.svg)

Where all roads meet is the `cordis_relation` table, this connects everything with everything, e.g.:

* project to programme
* project to organization
* organization to person
* project to publication
* publication to author (as string!)
* ...

`cordis_relation` records include the IDs and classnames of both the owner and owned entities as well as the relation type.



## Console output

The crawler prints log messages on the screen to inform you what is happening. These log messages contain a timestamp, a level and a message. Log level can be `TRACE`, `DEBUG`, `INFO`, `WARN` or `ERROR`. By default, `TRACE` and `DEBUG` are hidden, because they are useful only when some problem needs investigation, but in other cases they can be disturbing. You can turn them on by adding `-v` or `--verbose` option:

```bash
java -jar cordis-projects-crawler-VERSION-standalone.jar -v
```

If you are not interested in any of the log messages and you need no console output at all, you can use the `-q` or `--quiet` option:

```bash
java -jar cordis-projects-crawler-VERSION-standalone.jar -q
```

Note that this option will not have any effect if you turn on verbose mode.



## Aggressivity, networking

To reduce harm on [CORDIS][cordis] and [OpenAIRE][oaa] servers, the crawler **waits at least 2 seconds before sending another request to the same server**. This does not mean that the program sleeps 2 seconds everytime. After the processing of the previous request, the crawler calculates the required sleep time to ensure a delay of at least 2 seconds. If the processing took more time, then there will be no sleep.

When downloading result lists, the crawler requests the **maximum amount of results per request.** On CORDIS, it's 100, on OpenAIRE it's 10&nbsp;000 items. This reduces the number of requests needed to crawl a list. In case of OpenAIRE, only one request is sent per project, because I assume that  a project cannot have more than 10K publications.

CORDIS search results are requested in CSV format to reduce bandwith usage.

If there's an HTTP error, or an invalid response is received, the crawler increases the delay and retries the request.



## Crawl time

Especially when crawling a search result list or a large range of RCNs, you may want to estimate the time needed for the crawl. The main components of crawl time are the request delays and the transfer times of each request. Processing itself doesn't need significant amount of time, however when you are using the MySQL export feature, you can expect additional required time for the crawl to finish.

By default, there is only one request for each project, which downloads the CORDIS XML. You should count the 2 second delay and the request time for each:

```
N project by RCN = N * (2 second delay + request time)
```

When crawling also publications, there are obviously 2 request for each project, but the 2 second delay is only counted once, because the two requests are sent to different servers:

```
N project by RCN = N * (2 second delay + 2 * request time)
```

If you crawl projects using a search result list as seed, there are list page requests per every 100 project, but these are not significant if the seed returns hundreds of projects.

```
N project by RCN = N * (2 second delay + 2 * request time) + (N/100) * (2 second delay + request time)
```

Please note that request time depends on both your internet connection and the speed/health of the servers.

[cordis]: https://cordis.europa.eu/
[java]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[oaa]: http://api.openaire.eu/
[release]: https://github.com/juzraai/cordis-projects-crawler/releases/latest
[tsv]: https://en.wikipedia.org/wiki/Tab-separated_values