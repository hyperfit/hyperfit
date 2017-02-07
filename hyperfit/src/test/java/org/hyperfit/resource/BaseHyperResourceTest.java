package org.hyperfit.resource;


import org.hyperfit.resource.controls.link.HyperLink;
import org.junit.Test;



import static test.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * When building your own resource type you can copy paste this to your own test class
 * to get a good base going
 *
 * These tests exercise all the methods that depend upon the getLinks(String profile) implementation
 */
public class BaseHyperResourceTest {


// BEGIN getLink(String profile) tests

    @Test(expected = HyperResourceException.class)
    public void testGetLinkByRelZeroLinksForRel() {
        String rel = uniqueString();
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[0]).when(resource).getLinks(rel, false);

        resource.getLink(rel, false);
    }

    @Test
    public void testGetLinkByRelSingleLinkForRel() {
        String rel = uniqueString();

        HyperLink link = makeLink(rel);
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link}).when(resource).getLinks(rel, false);

        HyperLink result = resource.getLink(rel, false);
        assertEquals(link, result);
    }

    @Test(expected = HyperResourceException.class)
    public void testGetLinkByRelTwoLinksForRel() {
        String rel = uniqueString();

        HyperLink link1 = makeLink(rel);
        HyperLink link2 = makeLink(rel);
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link2, link1}).when(resource).getLinks(rel, false);

        resource.getLink(rel, false);

    }


    // BEGIN hasLink(String profile) tests
    @Test
    public void testHasLinkByRelZeroLinksForRel() {
        String rel = uniqueString();
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[0]).when(resource).getLinks(rel, false);

        assertFalse(resource.hasLink(rel));
    }

    @Test
    public void testHasLinkByRelSingleLinkForRel() {
        String rel = uniqueString();

        HyperLink link = makeLink(rel);
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link}).when(resource).getLinks(rel, false);

        assertTrue(resource.hasLink(rel));
    }

    @Test
    public void testHasLinkByRelTwoLinksForRel() {
        String rel = uniqueString();

        HyperLink link1 = makeLink(rel);
        HyperLink link2 = makeLink(rel);
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link2, link1}).when(resource).getLinks(rel, false);

        assertTrue(resource.hasLink(rel));

    }

    // BEGIN getLink(String profile, String name) tests
    @Test(expected = HyperResourceException.class)
    public void testGetLinkByRelAndNameZeroLinksForRel() {
        String rel = uniqueString();
        String name = uniqueString();
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[0]).when(resource).getLinks(rel, false);

        resource.getLink(rel, name, false);
    }

    @Test(expected = HyperResourceException.class)
    public void testGetLinkByRelAndName1LinkDifferentName() {
        String rel = uniqueString();
        String name = uniqueString();
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{makeLink(rel, uniqueString())}).when(resource).getLinks(rel, false);

        resource.getLink(rel, name, false);
    }

    @Test
    public void testGetLinkByRelAndNameSingleLinkMatchesName() {
        String rel = uniqueString();
        String name = uniqueString();

        HyperLink link = makeLink(rel, name);
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link}).when(resource).getLinks(rel, false);

        HyperLink result = resource.getLink(rel, name, false);
        assertEquals(link, result);
    }

    @Test(expected = HyperResourceException.class)
    public void testGetLinkByRelAndNameTwoLinksForRelNeitherMatch() {
        String rel = uniqueString();
        String name = uniqueString();

        HyperLink link1 = makeLink(rel, uniqueString());
        HyperLink link2 = makeLink(rel, uniqueString());
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link2, link1}).when(resource).getLinks(rel, false);

        resource.getLink(rel, name, false);

    }

    @Test
    public void testGetLinkByRelAndNameTwoLinksForRelSecondMatches() {
        String rel = uniqueString();
        String name = uniqueString();

        HyperLink link1 = makeLink(rel, uniqueString());
        HyperLink link2 = makeLink(rel, name);
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link1, link2}).when(resource).getLinks(rel, false);


        assertEquals(link2, resource.getLink(rel, name, false));

    }

    @Test(expected = HyperResourceException.class)
    public void testGetLinkByRelAndNameTwoLinksForRelBothMatch() {
        String rel = uniqueString();
        String name = uniqueString();

        HyperLink link1 = makeLink(rel, name);
        HyperLink link2 = makeLink(rel, name);
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link2, link1}).when(resource).getLinks(rel, false);

        resource.getLink(rel, name, false);

    }


    // BEGIN hasLink(String profile, String name) tests
    @Test
    public void testHasLinkByRelAndNameZeroLinksForRel() {
        String rel = uniqueString();
        String name = uniqueString();
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[0]).when(resource).getLinks(rel, false);

        assertFalse(resource.hasLink(rel, name));
    }

    @Test
    public void testHasLinkByRelAndName1LinkDifferentName() {
        String rel = uniqueString();
        String name = uniqueString();
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{makeLink(rel, uniqueString())}).when(resource).getLinks(rel, false);

        assertFalse(resource.hasLink(rel, name));
    }

    @Test
    public void testHasLinkByRelAndNameSingleLinkMatchesName() {
        String rel = uniqueString();
        String name = uniqueString();

        HyperLink link = makeLink(rel, name);
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link}).when(resource).getLinks(rel, false);

        assertTrue(resource.hasLink(rel, name));
    }

    @Test
    public void testHasLinkByRelAndNameTwoLinksForRelNeitherMatch() {
        String rel = uniqueString();
        String name = uniqueString();

        HyperLink link1 = makeLink(rel, uniqueString());
        HyperLink link2 = makeLink(rel, uniqueString());
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link2, link1}).when(resource).getLinks(rel, false);

        assertFalse(resource.hasLink(rel, name));

    }

    @Test
    public void testHasLinkByRelAndNameTwoLinksForRelSecondMatches() {
        String rel = uniqueString();
        String name = uniqueString();

        HyperLink link1 = makeLink(rel, uniqueString());
        HyperLink link2 = makeLink(rel, name);
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link1, link2}).when(resource).getLinks(rel, false);


        assertTrue(resource.hasLink(rel, name));

    }

    @Test
    public void testHasLinkByRelAndNameTwoLinksForRelBothMatch() {
        String rel = uniqueString();
        String name = uniqueString();

        HyperLink link1 = makeLink(rel, name);
        HyperLink link2 = makeLink(rel, name);
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link2, link1}).when(resource).getLinks(rel, false);

        assertTrue(resource.hasLink(rel));

    }


    // BEGIN getLinks(String profile, String name) tests
    @Test
    public void testGetLinksByRelAndNameZeroLinksForRel() {
        String rel = uniqueString();
        String name = uniqueString();
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[0]).when(resource).getLinks(rel, false);

        assertArrayEquals(new HyperLink[0], resource.getLinks(rel, name, false));
    }

    @Test
    public void testGetLinksByRelAndName1LinkDifferentName() {
        String rel = uniqueString();
        String name = uniqueString();
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{makeLink(rel, uniqueString())}).when(resource).getLinks(rel, false);

        assertArrayEquals(new HyperLink[0], resource.getLinks(rel, name, false));
    }

    @Test
    public void testGetLinksByRelAndNameSingleLinkMatchesName() {
        String rel = uniqueString();
        String name = uniqueString();

        HyperLink link = makeLink(rel, name);
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link}).when(resource).getLinks(rel, false);

        assertArrayEquals(new HyperLink[]{link}, resource.getLinks(rel, name, false));
    }

    @Test
    public void testGetLinksByRelAndNameTwoLinksForRelNeitherMatch() {
        String rel = uniqueString();
        String name = uniqueString();

        HyperLink link1 = makeLink(rel, uniqueString());
        HyperLink link2 = makeLink(rel, uniqueString());
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link2, link1}).when(resource).getLinks(rel, false);

        assertArrayEquals(new HyperLink[0], resource.getLinks(rel, name, false));

    }

    @Test
    public void testGetLinksByRelAndNameTwoLinksForRelSecondMatches() {
        String rel = uniqueString();
        String name = uniqueString();

        HyperLink link1 = makeLink(rel, uniqueString());
        HyperLink link2 = makeLink(rel, name);
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link1, link2}).when(resource).getLinks(rel, false);


        assertArrayEquals(new HyperLink[]{link2}, resource.getLinks(rel, name, false));

    }

    @Test
    public void testGetLinksByRelAndNameTwoLinksForRelBothMatch() {
        String rel = uniqueString();
        String name = uniqueString();

        HyperLink link1 = makeLink(rel, name);
        HyperLink link2 = makeLink(rel, name);
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(new HyperLink[]{link2, link1}).when(resource).getLinks(rel, false);

        assertArrayEquals(new HyperLink[]{link2, link1}, resource.getLinks(rel, name, false));

    }

    @Test
    public void testGetAsPathCallsOverload() {
        String path = uniqueString();
        String result = uniqueString();
        HyperResource resource = mock(BaseHyperResource.class, CALLS_REAL_METHODS);
        doReturn(result).when(resource).getPathAs(String.class, false, path);
        String actual = resource.getPathAs(String.class, path);

        assertEquals(result, actual);

    }


}
