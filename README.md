# hexJet
Simplifiying my geojson -> Uber h3 -> Neo4j processor; will port it to Hazelcast Jet, too.

## Explanation
This is a simplified version of my larger project, but it contains everything necessary to convert historical geoJson files to  Uber H3 hex tiles. 

## Time
There are some references to (:Year) nodes in the database. I have a calendar tree with Years, Months and Weeks made with this:

```
WITH range(1815, 2020) AS years
FOREACH (yr IN years |
  MERGE (y:Year{name:toString(yr), began:date({year:yr, month:1, day:1}), ended:date({year:yr, month:12, day:31}),
                last:date({year:yr, month:12, day:28}) })
  SET y.weeksThisYear = y.last.week,
  y.firstWeekBegins = date({year:yr,week:1}),
  y.lastWeekBegins = date({year:yr, week:y.weeksThisYear})
  FOREACH (wk IN range(1,y.weeksThisYear) |
    MERGE (w:Week{weekYear:((yr * 100) + wk), forYear:yr})
    SET w.name = toString(w.weekYear),
    w.began = date({year:yr, week:wk}),
    w.ended = date(w.began + duration('P5D') )
    CREATE (w)-[:PART_OF{weekCount:wk}]->(y)
  )
);
```

But, you could just create nodes for the few years connected to the data:

```
CREATE (:Year{name:"1815"})
CREATE (:Year{name:"1880"})
CREATE (:Year{name:"1914"})
CREATE (:Year{name:"1938"})
CREATE (:Year{name:"1945"})
CREATE (:Year{name:"1994"})
```



... and the results go something like this: 

```
/usr/lib64/jdk_Oracle/bin/java -javaagent:/home/cw/bin/idea-IU/lib/idea_rt.jar=43443:/home/cw/bin/idea-IU/bin -Dfile.encoding=UTF-8 -classpath /usr/lib64/jdk_Oracle/jre/lib/charsets.jar:/usr/lib64/jdk_Oracle/jre/lib/deploy.jar:/usr/lib64/jdk_Oracle/jre/lib/ext/cldrdata.jar:/usr/lib64/jdk_Oracle/jre/lib/ext/dnsns.jar:/usr/lib64/jdk_Oracle/jre/lib/ext/jaccess.jar:/usr/lib64/jdk_Oracle/jre/lib/ext/jfxrt.jar:/usr/lib64/jdk_Oracle/jre/lib/ext/localedata.jar:/usr/lib64/jdk_Oracle/jre/lib/ext/nashorn.jar:/usr/lib64/jdk_Oracle/jre/lib/ext/sunec.jar:/usr/lib64/jdk_Oracle/jre/lib/ext/sunjce_provider.jar:/usr/lib64/jdk_Oracle/jre/lib/ext/sunpkcs11.jar:/usr/lib64/jdk_Oracle/jre/lib/ext/zipfs.jar:/usr/lib64/jdk_Oracle/jre/lib/javaws.jar:/usr/lib64/jdk_Oracle/jre/lib/jce.jar:/usr/lib64/jdk_Oracle/jre/lib/jfr.jar:/usr/lib64/jdk_Oracle/jre/lib/jfxswt.jar:/usr/lib64/jdk_Oracle/jre/lib/jsse.jar:/usr/lib64/jdk_Oracle/jre/lib/management-agent.jar:/usr/lib64/jdk_Oracle/jre/lib/plugin.jar:/usr/lib64/jdk_Oracle/jre/lib/resources.jar:/usr/lib64/jdk_Oracle/jre/lib/rt.jar:/home/cw/Code/mason/jar/mason.19.jar:/home/cw/Code/masonLibraries/bsh-2.0b4.jar:/home/cw/Code/masonLibraries/itext-1.2.jar:/home/cw/Code/masonLibraries/jcommon-1.0.21.jar:/home/cw/Code/masonLibraries/jfreechart-1.0.17.jar:/home/cw/Code/masonLibraries/jmf.jar:/home/cw/Code/masonLibraries/portfolio.jar:/home/cw/Code/hexJet/target/classes:/home/cw/.m2/repository/com/uber/h3/3.2.0/h3-3.2.0.jar:/home/cw/.m2/repository/org/neo4j/neo4j/3.4.9/neo4j-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-kernel/3.4.9/neo4j-kernel-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-graphdb-api/3.4.9/neo4j-graphdb-api-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-resource/3.4.9/neo4j-resource-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-procedure-api/3.4.9/neo4j-procedure-api-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-kernel-api/3.4.9/neo4j-kernel-api-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-common/3.4.9/neo4j-common-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-values/3.4.9/neo4j-values-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-collections/3.4.9/neo4j-collections-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-primitive-collections/3.4.9/neo4j-primitive-collections-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-unsafe/3.4.9/neo4j-unsafe-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-io/3.4.9/neo4j-io-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-csv/3.4.9/neo4j-csv-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-logging/3.4.9/neo4j-logging-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-lucene-upgrade/3.4.9/neo4j-lucene-upgrade-3.4.9.jar:/home/cw/.m2/repository/org/apache/lucene/lucene-backward-codecs/5.5.0/lucene-backward-codecs-5.5.0.jar:/home/cw/.m2/repository/org/neo4j/neo4j-configuration/3.4.9/neo4j-configuration-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-index/3.4.9/neo4j-index-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-spatial-index/3.4.9/neo4j-spatial-index-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-ssl/3.4.9/neo4j-ssl-3.4.9.jar:/home/cw/.m2/repository/io/netty/netty-all/4.1.24.Final/netty-all-4.1.24.Final.jar:/home/cw/.m2/repository/org/bouncycastle/bcpkix-jdk15on/1.60/bcpkix-jdk15on-1.60.jar:/home/cw/.m2/repository/org/bouncycastle/bcprov-jdk15on/1.60/bcprov-jdk15on-1.60.jar:/home/cw/.m2/repository/org/neo4j/neo4j-lucene-index/3.4.9/neo4j-lucene-index-3.4.9.jar:/home/cw/.m2/repository/org/apache/lucene/lucene-analyzers-common/5.5.5/lucene-analyzers-common-5.5.5.jar:/home/cw/.m2/repository/org/apache/lucene/lucene-core/5.5.5/lucene-core-5.5.5.jar:/home/cw/.m2/repository/org/apache/lucene/lucene-queryparser/5.5.5/lucene-queryparser-5.5.5.jar:/home/cw/.m2/repository/org/apache/lucene/lucene-codecs/5.5.5/lucene-codecs-5.5.5.jar:/home/cw/.m2/repository/org/neo4j/neo4j-graph-algo/3.4.9/neo4j-graph-algo-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-udc/3.4.9/neo4j-udc-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher/3.4.9/neo4j-cypher-3.4.9.jar:/home/cw/.m2/repository/org/scala-lang/scala-library/2.11.12/scala-library-2.11.12.jar:/home/cw/.m2/repository/org/scala-lang/scala-reflect/2.11.12/scala-reflect-2.11.12.jar:/home/cw/.m2/repository/org/neo4j/neo4j-graph-matching/3.1.9/neo4j-graph-matching-3.1.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-codegen/3.4.9/neo4j-codegen-3.4.9.jar:/home/cw/.m2/repository/org/ow2/asm/asm/6.2/asm-6.2.jar:/home/cw/.m2/repository/org/ow2/asm/asm-util/6.2/asm-util-6.2.jar:/home/cw/.m2/repository/org/ow2/asm/asm-analysis/6.2/asm-analysis-6.2.jar:/home/cw/.m2/repository/org/ow2/asm/asm-tree/6.2/asm-tree-6.2.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-compiler-2.3/2.3.12/neo4j-cypher-compiler-2.3-2.3.12.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-frontend-2.3/2.3.12/neo4j-cypher-frontend-2.3-2.3.12.jar:/home/cw/.m2/repository/com/googlecode/concurrentlinkedhashmap/concurrentlinkedhashmap-lru/1.4.2/concurrentlinkedhashmap-lru-1.4.2.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-compiler-3.1/3.1.9/neo4j-cypher-compiler-3.1-3.1.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-frontend-3.1/3.1.9/neo4j-cypher-frontend-3.1-3.1.9.jar:/home/cw/.m2/repository/com/github/ben-manes/caffeine/caffeine/2.3.3/caffeine-2.3.3.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-compiler-3.3/3.3.7/neo4j-cypher-compiler-3.3-3.3.7.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-frontend-3.3/3.3.7/neo4j-cypher-frontend-3.3-3.3.7.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-ir-3.3/3.3.7/neo4j-cypher-ir-3.3-3.3.7.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-logical-plans-3.3/3.3.7/neo4j-cypher-logical-plans-3.3-3.3.7.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-util-3.4/3.4.9/neo4j-cypher-util-3.4-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-planner-3.4/3.4.9/neo4j-cypher-planner-3.4-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/openCypher-frontend-1/3.4.9/openCypher-frontend-1-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-expression-3.4/3.4.9/neo4j-cypher-expression-3.4-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-ir-3.4/3.4.9/neo4j-cypher-ir-3.4-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-logical-plans-3.4/3.4.9/neo4j-cypher-logical-plans-3.4-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-planner-spi-3.4/3.4.9/neo4j-cypher-planner-spi-3.4-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-runtime-util/3.4.9/neo4j-cypher-runtime-util-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-cypher-interpreted-runtime/3.4.9/neo4j-cypher-interpreted-runtime-3.4.9.jar:/home/cw/.m2/repository/org/parboiled/parboiled-scala_2.11/1.1.7/parboiled-scala_2.11-1.1.7.jar:/home/cw/.m2/repository/org/parboiled/parboiled-core/1.1.7/parboiled-core-1.1.7.jar:/home/cw/.m2/repository/net/sf/opencsv/opencsv/2.3/opencsv-2.3.jar:/home/cw/.m2/repository/org/neo4j/neo4j-jmx/3.4.9/neo4j-jmx-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-consistency-check/3.4.9/neo4j-consistency-check-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-command-line/3.4.9/neo4j-command-line-3.4.9.jar:/home/cw/.m2/repository/org/apache/commons/commons-text/1.3/commons-text-1.3.jar:/home/cw/.m2/repository/org/neo4j/neo4j-dbms/3.4.9/neo4j-dbms-3.4.9.jar:/home/cw/.m2/repository/org/neo4j/neo4j-import-tool/3.4.9/neo4j-import-tool-3.4.9.jar:/home/cw/.m2/repository/org/jprocesses/jProcesses/1.6.4/jProcesses-1.6.4.jar:/home/cw/.m2/repository/com/profesorfalken/WMI4Java/1.6.1/WMI4Java-1.6.1.jar:/home/cw/.m2/repository/com/profesorfalken/jPowerShell/1.9/jPowerShell-1.9.jar:/home/cw/.m2/repository/org/apache/commons/commons-compress/1.16.1/commons-compress-1.16.1.jar:/usr/lib64/jdk_Oracle/lib/tools.jar:/home/cw/.m2/repository/org/neo4j/neo4j-ogm-core/3.1.4/neo4j-ogm-core-3.1.4.jar:/home/cw/.m2/repository/org/apache/commons/commons-lang3/3.4/commons-lang3-3.4.jar:/home/cw/.m2/repository/io/github/lukehutch/fast-classpath-scanner/2.18.1/fast-classpath-scanner-2.18.1.jar:/home/cw/.m2/repository/org/neo4j/neo4j-ogm-api/3.1.4/neo4j-ogm-api-3.1.4.jar:/home/cw/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.7.1/jackson-databind-2.7.1.jar:/home/cw/.m2/repository/org/slf4j/slf4j-api/1.7.21/slf4j-api-1.7.21.jar:/home/cw/.m2/repository/org/neo4j/neo4j-ogm-bolt-driver/3.1.4/neo4j-ogm-bolt-driver-3.1.4.jar:/home/cw/.m2/repository/org/neo4j/driver/neo4j-java-driver/1.6.3/neo4j-java-driver-1.6.3.jar:/home/cw/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar:/home/cw/.m2/repository/de/grundid/opendatalab/geojson-jackson/1.8.1/geojson-jackson-1.8.1.jar:/home/cw/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.9.6/jackson-core-2.9.6.jar:/home/cw/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.9.6/jackson-annotations-2.9.6.jar:/home/cw/.m2/repository/edu/gmu/eclab/mason/19/mason-19.jar:/home/cw/.m2/repository/com/hazelcast/jet/hazelcast-jet/0.7/hazelcast-jet-0.7.jar:/home/cw/.m2/repository/org/jetbrains/annotations/16.0.3/annotations-16.0.3.jar edu.gmu.css.StreamApp
The factory started working at: 15:08:24.444
Nov 13, 2018 3:08:29 PM org.neo4j.driver.internal.logging.JULogger info
INFO: Direct driver instance 1100551705 created for server address localhost:7687

Saved Trinidad of 1938 to the database at 15:08:30.427
Saved Cyprus of 1938 to the database at 15:08:30.488
Saved Finland of 1938 to the database at 15:08:31.103
Saved Paraguay of 1938 to the database at 15:08:31.616
Saved Hungary of 1938 to the database at 15:08:31.735
Saved French Equatorial Africa of 1938 to the database at 15:08:34.782
Saved Muscat and Oman of 1938 to the database at 15:08:35.403
Saved Gambia, The of 1938 to the database at 15:08:35.438
Saved French Somaliland of 1938 to the database at 15:08:35.498
Saved Liberia of 1938 to the database at 15:08:35.744
Saved Brazil of 1938 to the database at 15:09:07.710
Saved United Kingdom of 1938 to the database at 15:09:07.865
Saved Malawi of 1938 to the database at 15:09:08.202
Saved Syria of 1938 to the database at 15:09:09.103
```
 . . .
 ```
 Saved Burma of 1938 to the database at 15:41:24.450
 Saved Rio De Oro of 1938 to the database at 15:41:30.449
 Completed 1938 at 15:41:30.717
 Saved Portugal of 1815 to the database at 15:41:38.555
 Saved Ottoman Empire of 1815 to the database at 15:42:05.363

```

. . .

```
Saved Travancore of 1815 to the database at 16:33:41.914
Saved Sweden of 1815 to the database at 16:33:55.393
Completed 1815 at 16:33:55.503
Saved Buganda of 1880 to the database at 16:34:03.640
Saved Griqualand West of 1880 to the database at 16:34:04.772
```
. . . 

```
Saved United Kingdom of 1880 to the database at 17:23:43.592
Saved Second Samori Empire of 1880 to the database at 17:23:45.817
Completed 1880 at 17:23:45.920
Saved Guyana of 1945 to the database at 17:23:54.243
Saved Poland of 1945 to the database at 17:23:54.251
```

. . . 