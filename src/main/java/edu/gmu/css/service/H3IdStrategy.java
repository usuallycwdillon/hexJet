package edu.gmu.css.service;

import edu.gmu.css.entities.Tile;
import org.neo4j.ogm.id.IdStrategy;

public class H3IdStrategy implements IdStrategy {

    public H3IdStrategy(){
    }

    @Override
    public Object generateId(Object entity) {
        Tile tile = (Tile) entity;
        return tile.getH3Id();
    }
}
