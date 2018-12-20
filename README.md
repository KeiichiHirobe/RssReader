Requirements
------------

* Java 8 or higher
* [sbt](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html)

Setup
-----

```bash
$ sbt
[info] Loading settings for project rssreader-build from assembly.sbt ...
[info] Loading project definition from ...
....
[info] sbt server started at ....

# compile and run
sbt:rssreader> run
[info] Running behiron.rssreader.RssReader 

    Usage: RssReader [--cut] [--convert] [ -o outputFile | --output outputFile] resourceName
...

```

Create a fat JAR
----------------

```bash
sbt:rssreader> assembly
[info] Strategy 'discard' was applied to 7 files (Run the task at debug level to see details)
[info] Strategy 'rename' was applied to a file (Run the task at debug level to see details)
[info] Packaging rssreader.jar ...
[info] Done packaging.
```

Usage
-----

```bash
# see help
$ java -jar ./target/scala-2.12/rssreader.jar --help

    Usage: RssReader [--cut] [--convert] [ -o outputFile | --output outputFile] resourceName

# read file and cut, print to stdout
$ java -jar ./target/scala-2.12/rssreader.jar /path/to/sample.txt  --cut
title: お試し就職制度を導入 body: ユーザベースに入社して約2ヶ月しか経ってませんが、ユーザベー
title: 【超新卒！イベントレ body: NewsPicksエンジニアの久保です。10/4(木)に、第

# get url and convert, cut, print to file
$ java -jar ./target/scala-2.12/rssreader.jar https://tech.uzabase.com/rss  --convert --cut  --output ./output
```
Test
-----
```bash
# check codestyle
sbt:rssreader> scalastyle
[info] scalastyle using config scalastyle-config.xml
[info] scalastyle Processed 8 file(s)
[info] scalastyle Found 0 errors
[info] scalastyle Found 0 warnings
[info] scalastyle Found 0 infos
[info] scalastyle Finished in 0 ms
[success]
# unittest
sbt:rssreader> test
[info] RssResorceSpec:
[info] RssTextResource
[info] - valid record parsed correctly
[info] - invalid record parsed as None
[info] TaskSpec:
[info] Task
[info] - convert
[info] - cut
[info] - convert must be done first
[info] Run completed in 366 milliseconds.
[info] Total number of tests run: 5
[info] Suites: completed 2, aborted 0
[info] Tests: succeeded 5, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success]

```

UML
-----
クラス図はトップディレクトリに`UML.pdf`として配置
