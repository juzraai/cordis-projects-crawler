# Crawler for CORDIS projects

*Version:* **2.0-SNAPSHOT** &middot; *Author:* Zsolt Jurányi ([web][www], [github][github])

---

## What's this?

This is a small and easy-to-use command line utility which can help you **grab [CORDIS][cordis] projects' information automatically,** along with related publications from [OpenAIRE][oaa]. You tell the program which projects you are interested in, and the software downloads the files which contain the required data. It can parse them and generate [TSV][tsv] or MySQL export.



## How to use it?

See the [User guide](USER_GUIDE.md).



## What's new in version 2?

The old [v1.3.0](https://github.com/juzraai/cordis-projects-crawler/releases/tag/v1.3.0) is not working anymore on the updated CORDIS website.

* I rebuilt the crawler from scratch, switched from Java to [Kotlin][kotlin].
* Extracts data from CORDIS XMLs and OpenAIRE API instead of messy HTML pages and encoded JSON strings.
* Can parse information of all projects, from oldest to newest.
* There are [new ways to tell](USER_GUIDE.md#seed) the crawler which projects are needed.
* [Modular architecture](DEVELOPER_GUIDE.md#modules) helps developers to extend/alter functionality of the crawler.
* Sexy documentation using [Docsify][docsify]



## Future plans, ideas

* Configuration: chunk (batch) size (`-b`)
* Configuration: request sleep (`-z`)
* Seed: RCN list file (`-s list:<filename>`)
* Export: RCN list file (`-l`)
* Seed: [CORDIS bulk file][bulk] (CSV, XLSX, ZIP) (`-s bulk:<filename>`)
* Reader: CORDIS bulk file (CSV, XLSX, ZIP), after default file cache
* Crawl project documents (`webItem`) (`-d`)
* Crawl result XMLs (`-r`)
* Generate "unified view" export (`-u`)
* Module registry: `addBefore(Class)` method
* Existing TSV exporters: reduce code redundancy



## Interested as a developer?

Read the [Developer guide](DEVELOPER_GUIDE.md) and [contributing instructions](CONTRIBUTING.md).

This project is licensed under [MIT License](LICENSE.md).



[bulk]: https://data.europa.eu/euodp/en/data/dataset?q=cordis
[cordis]: https://cordis.europa.eu/
[docsify]: https://docsify.js.org/#/quickstart
[github]: http://github.com/juzraai
[kotlin]: https://kotlinlang.org/
[oaa]: http://api.openaire.eu/
[release]: https://github.com/juzraai/cordis-projects-crawler/releases/latest
[tsv]: https://en.wikipedia.org/wiki/Tab-separated_values
[www]: http://juzraai.github.io/