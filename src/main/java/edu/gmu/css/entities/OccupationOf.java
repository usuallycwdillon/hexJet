package edu.gmu.css.entities;

import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type="OCCUPATION_OF")
public class OccupationOf {

    @Id @GeneratedValue
    private Long relationshipId;
    @Property
    private Integer year;
    @StartNode
    Territory territory;
    @EndNode
    Tile tile;

    public OccupationOf() {}

    public OccupationOf(Territory territory, Tile tile, Integer year) {
        this.territory = territory;
        this.tile = tile;
        this.year = year;
    }
}
