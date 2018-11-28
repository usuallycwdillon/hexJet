package edu.gmu.css.service;

import edu.gmu.css.entities.Territory;
import org.neo4j.ogm.session.Session;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TerritoryServiceImpl extends GenericService<Territory> implements TerritoryService {

    private static final int DEPTH_LIST = 0;
    private static final int DEPTH_ENTITY = 0;
    protected Session session = Neo4jSessionFactory.getInstance().getNeo4jSession();

    @Override
    public Iterable<Territory> findAll() {
        return session.loadAll(Territory.class, DEPTH_LIST);
    }

    public Territory find(Long mapKey) {
        return session.load(getEntityType(), DEPTH_ENTITY);
    }

    public void delete (Long mapKey) {
        session.delete(session.load(getEntityType(), mapKey));
    }

    @Override
    public Territory createOrUpdate(Territory territory) {
        session.save(territory, DEPTH_ENTITY);
        return find(territory.getId());
    }

    @Override
    Class<Territory> getEntityType() {
        return Territory.class;
    }

    public Map<String, Territory> getMap() {
        Iterable<Territory> tList = findAll();
        Map<String, Territory> map = StreamSupport.stream(tList.spliterator(), false)
                .collect(Collectors.toMap(Territory::getMapKey, t -> t));
        return map;
    }
}


