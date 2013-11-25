CORDIS Projects Crawler
=======================

With this app/API you can fetch information of all [CORDIS projects](http://cordis.europa.eu/projects).

Users can use the binary with CLI, developers can use the classes! :)

####Current version: 1.0.1-SNAPSHOT


##1. Features

* can download project data page and publication list JSON string
* two download mode: download all, or download one by RCN
* download files into output directory
* can skip already existing files
* filename templates can be configured
* can download JSONs for existing project data pages
* CLI for set up and run crawler


##2. Requirements

* JRE 1.6


##3. Usage

###3.1. Users

Download all projects' information to "outputdir" directory:
```
java -jar cordis-crawler-VERSION.jar -a -d outputdir/
```

Download only project with RCN 90433:
```
java -jar cordis-crawler-VERSION.jar -1 90433 -d outputdir/
```

To see detailed usage information, run the binary with no arguments.


###3.2. Developers

Download all projects' information to "outputdir" directory:
```java
List<Project> projects = 
	new ProjectDownloader()
		.outputDir("outputdir/")
		.all();
```

Download only project with RCN 90433:
```java
Project project =
	new ProjectDownloader()
		.outputDir("outputdir/")
		.byRCN(90433);
```

All possible options:
```java
	new ProjectDownloader()
		.outputDir("outputdir/")	// sets output directory
		.projectFilename("%d.html")	// sets project data page filename
		.publistFilename("%d.json")	// sets publication list JSON filename
		.readRCNsFromDirectory()	// when you need only the JSONs
		.skipExisting(false)		// turns on re-downloading
		.all(); // .byRCN(90433);
```

Javadoc will come soon! :-)


##4. Future plans, ideas

* parse the whole project data page
* parse JSON objects
* export project and publications data to XML or database


##5. Thanks to

Thanks to [ravindraharige](https://github.com/ravindraharige/cordis-crawler) for the XML download URL!
