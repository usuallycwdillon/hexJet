package edu.gmu.css.entities;

import com.uber.h3core.H3Core;
import edu.gmu.css.service.H3IdStrategy;
import org.neo4j.ogm.annotation.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@NodeEntity
public class Tile extends Entity implements Serializable {
    /**
     * Tiles are implemented as hexagonal (and 12 pentagonal) territorial units defined by Uber H3 Hierarchical Discrete
     * Global Grid object boundaries. Only land tiles are implemented.
     */
    @Id @GeneratedValue (strategy = H3IdStrategy.class)
    private Long h3Id;
    private String address;

    @Relationship(type="ABUTS", direction = Relationship.UNDIRECTED)
    private Set<Tile> neighbors = new HashSet<>();

    private List<Long>neighborIds = new ArrayList<>();

    public Tile() {
    }

    public Tile(Long h3Id) {
        this.h3Id = h3Id;
        learnNeighborhood();
    }

    public String getAddress() {
        return address;
    }

    public Long getH3Id() {return h3Id; }

    public Set<Tile> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(Tile n) {
        this.neighbors.add(n);
    }

    private void learnNeighborhood() {
        try {
            H3Core h3 = H3Core.newInstance();
            this.neighborIds = h3.kRing(h3Id, 1);
            this.neighborIds.remove(h3Id);
            // unrelated, but I'm taking advantage of having already initiated an H3 instance
            this.address = h3.h3ToString(h3Id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Long> getNeighborIds() {
        return this.neighborIds;
    }
}
