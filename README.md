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
