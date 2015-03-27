package org.hyperfit.resource;

import org.hyperfit.annotation.Profiles;

import java.util.*;

public class ProfileBasedInterfaceSelectionStrategy implements InterfaceSelectionStrategy {

    private final Map<String,List<Class<? extends HyperResource>>> registry;

    public ProfileBasedInterfaceSelectionStrategy(Collection<Class<? extends HyperResource>> classes) {

        HashMap<String,List<Class<? extends HyperResource>>> map = new HashMap<String, List<Class<? extends HyperResource>>>();

        for (Class<? extends HyperResource> clazz : classes) {
            Profiles profileAnny = clazz.getAnnotation(Profiles.class);
            if(profileAnny != null){
                for(String profile : profileAnny.value()){
                    if(!map.containsKey(profile)){
                        map.put(profile, new ArrayList<Class<? extends HyperResource>>());
                    }

                    map.get(profile).add(clazz);
                }
            }

        }

        registry = Collections.unmodifiableMap(map);
    }

    public Class<?>[] determineInterfaces(Class<?> expectedInterface, HyperResource resourceToWrap) {
        HashSet<Class<?>> matchingResources = new HashSet<Class<?>>();
        matchingResources.add(expectedInterface);

        for(String profile : resourceToWrap.getProfiles()){
            if(registry.containsKey(profile)){
                matchingResources.addAll(registry.get(profile));
            }
        }

        return matchingResources.toArray(new Class<?>[matchingResources.size()]);

    }
}
