package org.hyperfit.resource;

import org.junit.Test;

import java.util.LinkedHashSet;

import static org.junit.Assert.*;

public class SimpleInterfaceSelectionStrategyTest {

    @Test
    public void testDetermineInterfaces(){
        SimpleInterfaceSelectionStrategy x = new SimpleInterfaceSelectionStrategy();


        HyperResource facet = new BaseHyperResource(){

            public HyperLink[] getLinks() {
                return new HyperLink[0];
            }

            public HyperLink[] getLinks(String relationship) {
                return new HyperLink[0];
            }

            public <T> T getPathAs(Class<T> classToReturn, String... path) {
                return null;
            }

            public boolean hasPath(String... path) {
                return false;
            }

            public boolean canResolveLinkLocal(String relationship) {
                return false;
            }

            public HyperResource resolveLinkLocal(String relationship) {
                return null;
            }

            public HyperResource[] resolveLinksLocal(String relationship) {
                return new HyperResource[0];
            }

            public boolean isMultiLink(String relationship) {
                return false;
            }

            public LinkedHashSet<String> getProfiles() {
                return null;
            }
        };

        Class<?>[] result = x.determineInterfaces(facet.getClass(), facet);

        assertArrayEquals(new Class<?>[]{facet.getClass()}, result);

    }
}
