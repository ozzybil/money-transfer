package com.assignment.db;

import com.assignment.model.Entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory data store for Entity objects
 * Keeps a {@link ConcurrentHashMap} for key-value pairs
 * Add operation might be called from different clients, meaning that multiple threads may access to Map concurrently
 * ConcurrentHasMap has been chosen as the concrete Map implementation
 *    in order to prevent attempts of adding multiple entities with the same id
 * Entity.id is used as the key in map
 *
 * @param <T> the type store values
 */
public class InMemoryDb<T extends Entity> {

    // Map object to use
    private Map<String, T> entities;

    public InMemoryDb() {
        entities = new ConcurrentHashMap<>();
    }

    /**
     * Saves given entity if there isn't any other entity with the same id
     *
     * @param entity to be added
     * @return true if entity is absent in the map and added successfully
     */
    public boolean add(T entity) {

        if (entity == null || entity.getId() == null) {
            return false;
        }

        T previous = entities.putIfAbsent(entity.getId(), entity);

        return previous == null;
    }

    /**
     * Returns the entity with specified id or {@code null} if this store does not contain no entity with the id
     *
     * @param id to be search
     * @return the entity with specified id, otherwise {@code null}
     */
    public T get(String id) {

        if (id == null) {
            return null;
        }

        return entities.get(id);
    }

    /**
     * Returns all the entities in read-only format
     *
     * @return all entities in this store
     */
    public Collection<T> getAll() {
        return Collections.unmodifiableCollection(entities.values());
    }
}