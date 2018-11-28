package edu.gmu.css;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.uber.h3core.H3Core;
import com.uber.h3core.util.GeoCoord;
import edu.gmu.css.entities.Territory;
import edu.gmu.css.service.Neo4jSessionFactory;
import edu.gmu.css.service.TerritoryServiceImpl;
import org.geojson.*;
import org.neo4j.ogm.session.Session;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//import edu.gmu.css.service.TerritoryServiceImpl;

public class GeoJSONFactory {

    public static void main(String args[]) {
        int [] years = {1815, 1880, 1914, 1938, 1945, 1994};

        LocalTime startTime = LocalTime.now();
        System.out.println("The program started working at: " + startTime);

        Session session = Neo4jSessionFactory.getInstance().getNeo4jSession();
        LocalTime sessionTime = LocalTime.now();
        System.out.println("The session was initialized at: " + sessionTime);

        Stream<Territory> territoryStream = session.loadAll(Territory.class, 0).stream();

        LocalTime collectionTime = LocalTime.now();
        System.out.println("The collection was retrieved at: " + collectionTime);



        System.exit(0);
    }



}
