package org.hyperfit.resource.registry;

import org.hyperfit.annotation.Profiles;
import org.hyperfit.resource.HyperResource;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Carlos Perez
 */
public class ProfileResourceRegistryIndexStrategy implements ResourceRegistry.IndexStrategy<String> {

    public boolean canHandle(Class<? extends HyperResource> clazz) {
        Profiles profilesAnnotation = clazz.getAnnotation(Profiles.class);
        return profilesAnnotation != null;
    }

    public Set<String> getKeys(Class<? extends HyperResource> clazz) {
        Set<String> keySet = new HashSet<String>();
        Profiles profilesAnnotation = clazz.getAnnotation(Profiles.class);
        Collections.addAll(keySet, profilesAnnotation.value());

        return keySet;
    }

}
