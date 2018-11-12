package edu.gmu.css;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.gmu.css.data.DatasetData;
import edu.gmu.css.entities.Dataset;
import edu.gmu.css.entities.Territory;
import edu.gmu.css.entities.Tile;
import edu.gmu.css.service.Neo4jSessionFactory;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.ogm.session.Neo4jSession;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.transaction.Transaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class StreamApp {

    private static Map<Integer, Dataset> geodatasets = DatasetData.GEODATASETS;
    private static int resolution = 4;
    private static int unclaimedIndex = 0;

    public static Map<String, Territory> territories = new HashMap<>();
    public static Map<Long, Tile> globalHexes = new HashMap<>();
    public static Map<Long, Tile> missingHexes = new HashMap<>();


    public static void main( String[] args ) {
        LocalTime startTime = LocalTime.now();
        System.out.println("The factory started working at: " + startTime);

        for (Map.Entry entry : geodatasets.entrySet()) {

            int y = (int)entry.getKey();
            Dataset d = (Dataset) entry.getValue();
            String filename = "cowWorld_" + y + ".geojson";

            List<Feature> features = geoJsonProcessor(filename);

            List<Territory> territoryList = features.stream()
                    .map(feature -> new Territory((Feature)feature, y))
                    .collect(Collectors.toList());

            d.addAllFacts(territoryList);

            for (Territory t : territoryList) {
                Session session = Neo4jSessionFactory.getInstance().getNeo4jSession();
                Transaction tx = session.beginTransaction();
                session.save(t, 2);
                tx.commit();
                session.clear();
                System.out.println("Saved " + t.getMapKey() + " to the database at " + LocalTime.now());
            }

            Session session = Neo4jSessionFactory.getInstance().getNeo4jSession();
            Transaction tx = session.beginTransaction();
            session.save(d, 1);
            tx.commit();
            session.clear();
            System.out.println("Completed " + y + " at " + LocalTime.now());
        }

        new StreamApp().joinHexes();

        for (int year : geodatasets.keySet()) {
            new StreamApp().findTerritoryNeighbors(year);
        }

    }

    private static List<Feature> geoJsonProcessor(String filename) {

        // Parse the GeoJSON file
        String filepath = "src/main/resources/historicalBasemaps/" + filename;
        File file = new File(filepath);

        List<Feature> features = new ArrayList<>();
        try (InputStream inputStream = new FileInputStream(file)) {
            features = new ObjectMapper().readValue(inputStream, FeatureCollection.class).getFeatures();
        } catch (Exception e) {
            System.out.println("Well, that didn't work. Was it on the right path?: " + filepath);
            e.printStackTrace();
        }
        return features;
    }

    private static Map.Entry<String, Territory> featureEntryProcessor(Feature feature, int year) {
        Territory territory = new Territory(feature, year);

        Map.Entry<String, Territory> entry = new Map.Entry<String, Territory>() {
            @Override
            public String getKey() {
                return territory.getName() + " of " + year;
            }

            @Override
            public Territory getValue() {
                return territory;
            }

            @Override
            public Territory setValue(Territory value) {
                return territory;
            }
        };

        return entry;
    }

    private void joinHexes() {

        System.out.println("The time is " + LocalTime.now() + " and it's about to lattice them hexes...");

        Iterator it = globalHexes.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            updateHexNeighbors((Long)pair.getKey());

        }

        if (missingHexes.size() != 0) {
            globalHexes.putAll(missingHexes);
        }

        System.out.println("There were " + missingHexes.size() + " missing hex Tiles.");
    }

    private void findTerritoryNeighbors(int year) {

        System.out.println("The simulation is ready to find territory neighbors for " + year + " at: " + LocalTime.now());

        String query = "MATCH (t1:Territory{year:{year}})-[:OCCUPATION_OF]->(h1:Tile)-[:ABUTS]-(h2:Tile)<-[:OCCUPATION_OF]-(t2:Territory{year:{year}})\n" +
                "WHERE t1 <> t2 AND t1.year = t2.year\n" +
                "MATCH (y:Year{name:toString({year})})\n" +
                "WITH t1, t2, y\n" +
                "MERGE (t1)-[b:BORDERS{during:{year}}]->(t2)\n" +
                "MERGE (t1)-[d:DURING]->(y)\n" +
                "MERGE (t2)-[e:DURING]->(y)";

        Neo4jSessionFactory.getInstance().getNeo4jSession().query(query, MapUtil.map("year", year));
    }

    private void updateHexNeighbors(Long h) {
        Tile tile = globalHexes.get(h);

        for (Long n : tile.getNeighborIds()) {
            if (globalHexes.containsKey(n)) {
                Tile neighbor = globalHexes.get(n);
                tile.addNeighbor(globalHexes.get(n));
            } else {
                Tile newHex = new Tile(n);
                missingHexes.put(n, newHex);
                tile.addNeighbor(newHex);
            }
        }
        Session session = Neo4jSessionFactory.getInstance().getNeo4jSession();
        Transaction tx = session.beginTransaction();
        session.save(tile, 1);
        tx.commit();
        session.clear();
    }

}
