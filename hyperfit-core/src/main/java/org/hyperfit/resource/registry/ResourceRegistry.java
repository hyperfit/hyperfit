package org.hyperfit.resource.registry;

import org.hyperfit.resource.HyperResource;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores resource classes, and it caches them in a way that lookups are O(1)
 *
 * @author Carlos Perez
 */
public class ResourceRegistry {

    private final ConcurrentHashMap<IndexStrategy, ConcurrentHashMap<Object, Class<? extends HyperResource>>> registryMap =
            new ConcurrentHashMap<IndexStrategy, ConcurrentHashMap<Object, Class<? extends HyperResource>>>();

    //Allows having a single instance per adding strategy class.
    private final ConcurrentHashMap<Class<? extends IndexStrategy>, IndexStrategy> indexStrategyClassMap = new ConcurrentHashMap<Class<? extends IndexStrategy>, IndexStrategy>();

    public ResourceRegistry(IndexStrategy... addingStrategies) {
        if (addingStrategies == null || addingStrategies.length == 0) {
            throw new RuntimeException("there must be at least one adding strategy");
        }

        for (IndexStrategy indexStrategy : addingStrategies) {
            registryMap.putIfAbsent(indexStrategy, new ConcurrentHashMap<Object, Class<? extends HyperResource>>());
            indexStrategyClassMap.putIfAbsent(indexStrategy.getClass(), indexStrategy);
        }
    }

    //adds a collection of classes only if accepted by the AddingStrategy
    public ResourceRegistry add(Collection<Class<? extends HyperResource>> classes) {

        for (Class<? extends HyperResource> clazz : classes) {
            for (Map.Entry<IndexStrategy, ConcurrentHashMap<Object, Class<? extends HyperResource>>> registryMapEntry : registryMap.entrySet()) {
                IndexStrategy indexStrategy = registryMapEntry.getKey();
                if (indexStrategy.canHandle(clazz)) {
                    for (Object key : indexStrategy.getKeys(clazz)) {
                        registryMapEntry.getValue().putIfAbsent(key, clazz);
                    }
                }
            }
        }

        return this;
    }

    /**
     * Gets the resource class using a search strategy
     */
    public <T> Class<? extends HyperResource> getResourceClass(RetrievalStrategy<T> retrievalStrategy, T value) {

        IndexStrategy indexStrategy = indexStrategyClassMap.get(retrievalStrategy.getIndexStrategyClass());
        if (indexStrategy != null) {
            Map<Object, Class<? extends HyperResource>> resourceClassMap = registryMap.get(indexStrategy);

            if (resourceClassMap != null && !resourceClassMap.isEmpty()) {
                return retrievalStrategy.retrieve(resourceClassMap, value);
            }
        }

        return null;
    }

    /**
     * Determines how to add a resource class into registryMap
     */
    public interface IndexStrategy<T> {
        boolean canHandle(Class<? extends HyperResource> clazz);

        //these are the keys that will identify a single hyper resource class
        Set<T> getKeys(Class<? extends HyperResource> clazz);
    }

    /**
     * Allows getting a class from the resource registry in a certain fashion using the
     * strategy pattern
     */
    public interface RetrievalStrategy<T> {
        //returns the key for registryMap
        Class<? extends IndexStrategy> getIndexStrategyClass();

        /**
         * processes a value and looks for a related class in resourceClassMap, which is just
         * registryMap.get(getIndexStrategyClass()).
         * A map is being used in case we need to iterate it or just get some value from it
         */
        Class<? extends HyperResource> retrieve(Map<Object, Class<? extends HyperResource>> resourceClassMap, T value);
    }

}

