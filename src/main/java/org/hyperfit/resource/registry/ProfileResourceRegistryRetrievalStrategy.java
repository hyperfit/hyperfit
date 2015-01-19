package org.hyperfit.resource.registry;

import org.hyperfit.resource.HyperResource;
import org.javatuples.Pair;

import java.util.*;

/**
 * This is a strategy that allows retrieving from the resource registry
 * the class related to the last profile that "works". In other words
 * the profiles are used as keys and if the class obtained is a subtype of a Class<T>
 * then we found the class we are looking for,
 *
 * @author Carlos Perez
 */
public class ProfileResourceRegistryRetrievalStrategy<T extends HyperResource> implements ResourceRegistry.RetrievalStrategy<Pair<Class<T>, HyperResource>> {

    public Class<? extends ResourceRegistry.IndexStrategy> getIndexStrategyClass() {
        return ProfileResourceRegistryIndexStrategy.class;
    }

    /**
     * @param resourceClassMap Where to look
     * @param resourcePair     Takes the parent type and the hyper resource
     * @return the found class
     */
    public Class<T> retrieve(Map<Object, Class<? extends HyperResource>> resourceClassMap, Pair<Class<T>, HyperResource> resourcePair) {

        Class<T> type = resourcePair.getValue0();
        HyperResource resource = resourcePair.getValue1();

        LinkedHashSet<String> profileSet = resource.getProfiles();
        ListIterator<String> profileListIterator =
                new ArrayList<String>(profileSet).listIterator(profileSet.size());

        //iterate in reverse order to get the last profile that works
        while (profileListIterator.hasPrevious()) {
            String profile = profileListIterator.previous();
            Class<? extends HyperResource> subClass = resourceClassMap.get(profile);

            if (null != subClass && type.isAssignableFrom(subClass)) {
                return (Class<T>) subClass;
            }
        }

        return type;
    }

}
