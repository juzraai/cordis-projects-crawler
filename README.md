CORDIS Projects Crawler
=======================

With this app/API you can fetch information of all [CORDIS projects](http://cordis.europa.eu/projects).

Users can use the CLI, developers can use the classes. (JavaDoc will come soon! :-) )

####Current version: 1.0.0-SNAPSHOT


##Features

* can download project data page and publication list JSON string
* two download mode: download all, or download one by RCN
* download files into output directory
* can skip already existing files
* filename templates can be configured
* CLI for set up and run crawler


##Usage

Download all projects's information to "outputdir" directory:
```
java -jar cordis-crawler-VERSION.jar -a -d outputdir/
```

Download only project with RCN 90433:
```
java -jar cordis-crawler-VERSION.jar -1 90433 -d outputdir/
```

To see detailed usage information, run the binary with no arguments.


##Future plans

* parse JSON objects
* export project and publications data to XML or database


##Thanks to

Thanks to [ravindraharige](https://github.com/ravindraharige/cordis-crawler) for the XML download URL!
