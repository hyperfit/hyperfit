package org.hyperfit.resource.registry;


import org.hyperfit.annotation.Profiles;
import org.hyperfit.resource.HyperResource;
import org.javatuples.Pair;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;


public class ResourceRegistryTest {

    @Profiles({"a", "b"})
    public interface SomeResource extends HyperResource {
    }

    public interface SomeResource2 extends HyperResource {
    }

    public interface SomeResource3 extends HyperResource {
    }

    @Test(expected = RuntimeException.class)
    public void testNoIndexStrategyException() {
        new ResourceRegistry();
    }

    @Test
    public void testRetrieve() {
        ResourceRegistry.IndexStrategy mockIndexStrategy = mock(ResourceRegistry.IndexStrategy.class);
        ResourceRegistry.RetrievalStrategy mockRetrievalStrategy = mock(ResourceRegistry.RetrievalStrategy.class);

        final String value = "aValue";

        when(mockIndexStrategy.getKeys(SomeResource.class)).thenReturn(createStringSet(""));
        when(mockIndexStrategy.getKeys(SomeResource2.class)).thenReturn(createStringSet(""));
        when(mockIndexStrategy.doAccept(SomeResource.class)).thenReturn(true);
        when(mockIndexStrategy.doAccept(SomeResource2.class)).thenReturn(true);

        when(mockRetrievalStrategy.getIndexStrategyClass()).thenReturn(mockIndexStrategy.getClass());
        when(mockRetrievalStrategy.retrieve(anyMap(), eq(value))).thenReturn(SomeResource.class);

        ResourceRegistry resourceRegistry = new ResourceRegistry(mockIndexStrategy);

        Set<Class<? extends HyperResource>> classSet = new HashSet<Class<? extends HyperResource>>();
        classSet.add(SomeResource.class);
        classSet.add(SomeResource2.class);
        resourceRegistry.add(classSet);

        assertEquals(SomeResource.class, resourceRegistry.getResourceClass(mockRetrievalStrategy, value));
    }

    @Test
    public void testProfileIndexStrategy() {
        ProfileResourceRegistryIndexStrategy indexStrategy = new ProfileResourceRegistryIndexStrategy();
        Set<String> profiles = new HashSet<String>();
        profiles.add("a");
        profiles.add("b");

        assertTrue(indexStrategy.doAccept(SomeResource.class));
        assertEquals(profiles, indexStrategy.getKeys(SomeResource.class));
    }

    @Test(expected = NullPointerException.class)
    public void testProfileIndexStrategyException() {
        new ProfileResourceRegistryIndexStrategy().getKeys(null);
    }

    @Test
    public void testProfileRetrievalStrategy() {
        ProfileResourceRegistryIndexStrategy mockIndexStrategy = mock(ProfileResourceRegistryIndexStrategy.class);
        ProfileResourceRegistryRetrievalStrategy retrievalStrategy = spy(new ProfileResourceRegistryRetrievalStrategy());
        when(retrievalStrategy.getIndexStrategyClass()).thenReturn(mockIndexStrategy.getClass());

        HyperResource mockResource = mock(HyperResource.class);

        String[] profiles = {"profile1", "profile2", "profile2.1"};

        LinkedHashSet<String> profileSet = createStringSet(profiles);

        when(mockResource.getProfiles()).thenReturn(profileSet);

        when(mockIndexStrategy.doAccept(SomeResource.class)).thenReturn(true);
        when(mockIndexStrategy.doAccept(SomeResource2.class)).thenReturn(true);
        when(mockIndexStrategy.doAccept(SomeResource3.class)).thenReturn(true);

        when(mockIndexStrategy.getKeys(SomeResource.class)).thenReturn(createStringSet(profiles[0]));
        when(mockIndexStrategy.getKeys(SomeResource2.class)).thenReturn(createStringSet(profiles[1], profiles[2]));

        ResourceRegistry resourceRegistry = new ResourceRegistry(mockIndexStrategy);

        Set<Class<? extends HyperResource>> classSet = new HashSet<Class<? extends HyperResource>>();
        classSet.add(SomeResource.class);
        classSet.add(SomeResource2.class);
        classSet.add(SomeResource3.class);
        resourceRegistry.add(classSet);

        assertEquals(SomeResource2.class, resourceRegistry.getResourceClass(retrievalStrategy, Pair.with(HyperResource.class, mockResource)));
    }


    @Test
    public void testProfileIndexAndRetrievalStrategy() {

        LinkedHashSet<String> profiles = new LinkedHashSet<String>();
        profiles.add("a");
        profiles.add("b");

        HyperResource mockResource = mock(HyperResource.class);
        when(mockResource.getProfiles()).thenReturn(profiles);

        ProfileResourceRegistryIndexStrategy profileResourceRegistryIndexStrategy = new ProfileResourceRegistryIndexStrategy();
        ProfileResourceRegistryRetrievalStrategy profileResourceRegistryRetrievalStrategy = new ProfileResourceRegistryRetrievalStrategy();

        ResourceRegistry resourceRegistry = new ResourceRegistry(profileResourceRegistryIndexStrategy);

        Set<Class<? extends HyperResource>> classSet = new HashSet<Class<? extends HyperResource>>();
        classSet.add(SomeResource.class);
        classSet.add(SomeResource2.class);
        classSet.add(SomeResource3.class);
        resourceRegistry.add(classSet);

        assertEquals(SomeResource.class, resourceRegistry.getResourceClass(profileResourceRegistryRetrievalStrategy, Pair.with(HyperResource.class, mockResource)));
    }

    private <T> LinkedHashSet<T> createStringSet(T... strs) {
        return new LinkedHashSet<T>(Arrays.asList(strs));
    }

}
