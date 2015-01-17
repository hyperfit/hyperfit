package org.hyperfit.resource.hal.json;


import org.hyperfit.exception.HyperClientException;
import org.hyperfit.resource.HyperLink;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.HyperResourceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import static com.bodybuilding.commerce.hyper.client.TestHelpers.makeSet;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.UUID;


public class HalJsonResourceTest {

    static JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
    ObjectNode root;
    ObjectNode links;
    ObjectNode embedded;

    @Before
    public void setUp(){
        root = nodeFactory.objectNode();
        links = nodeFactory.objectNode();
        embedded = nodeFactory.objectNode();
        root.put("_links", links);
        root.put("_embedded", embedded);
    }

    public static ObjectNode makeValidLinkNode(){
        ObjectNode linkNode = nodeFactory.objectNode();
        linkNode.put("href", "http://url/to/" + UUID.randomUUID().toString());

        return linkNode;
    }

    @Test(expected = NullPointerException.class)
    public void testDataSourceNull() {
        new HalJsonResource((JsonNode)null);
    }

    @Test
    public void testGetLinkWithSingleResource() {
        String linkHref = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions";
        String title = "a title";

        ObjectNode promosLink = nodeFactory.objectNode();
        links.put("bb:promotions", promosLink);
        promosLink.put("href", linkHref);
        promosLink.put("title", title);

        HalJsonResource resource = new HalJsonResource(root);
        HyperLink hyperLink = resource.getLink("bb:promotions");
        assertEquals(linkHref, hyperLink.getHref());
        assertEquals(title, hyperLink.getTitle());
        assertEquals(null, hyperLink.getName());
    }

    @Test
    public void testGetLinkWithOneResourceArray() {
        ArrayNode linkArray = nodeFactory.arrayNode();
        ObjectNode link = nodeFactory.objectNode();
        link.put("href", "/only-item");
        link.put("title", "only-item-title");
        linkArray.add(link);

        links.put("bb:one", linkArray);

        HalJsonResource resource = new HalJsonResource(root);
        HyperLink hyperLink = resource.getLink("bb:one");
        assertEquals("/only-item", hyperLink.getHref());
        assertEquals("only-item-title", hyperLink.getTitle());
        assertEquals(null, hyperLink.getName());
    }

    @Test(expected = HyperResourceException.class)
    public void testGetLinkWithMultipleResourcesArray() {
        ArrayNode linkArray = nodeFactory.arrayNode();
        ObjectNode link1 = nodeFactory.objectNode();
        ObjectNode link2 = nodeFactory.objectNode();
        link1.put("href", "/first-item");
        link1.put("title", "first-item-title");
        link2.put("href", "/second-item");
        link2.put("title", "second-item-title");
        linkArray.add(link1);
        linkArray.add(link2);

        links.put("bb:multi", linkArray);

        HalJsonResource resource = new HalJsonResource(root);
        resource.getLink("bb:multi");
    }

    @Test
    public void testGetLinkWithSingleResourceAndName() {
        String linkHref = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions";
        String title = "a title";
        String name = "a value";

        ObjectNode promosLink = nodeFactory.objectNode();
        links.put("bb:promotions", promosLink);
        promosLink.put("href", linkHref);
        promosLink.put("title", title);
        promosLink.put("name", name);

        HalJsonResource resource = new HalJsonResource(root);
        HyperLink hyperLink = resource.getLink("bb:promotions", name);
        assertEquals(linkHref, hyperLink.getHref());
        assertEquals(title, hyperLink.getTitle());
        assertEquals(name, hyperLink.getName());
    }

    @Test
    public void testGetLinkWithOneResourceArrayAndName() {
        ArrayNode linkArray = nodeFactory.arrayNode();
        ObjectNode link = nodeFactory.objectNode();
        link.put("href", "/only-item");
        link.put("title", "only-item-title");
        link.put("name", "only-item-value");
        linkArray.add(link);

        links.put("bb:one", linkArray);

        HalJsonResource resource = new HalJsonResource(root);
        HyperLink hyperLink = resource.getLink("bb:one", "only-item-value");
        assertEquals("/only-item", hyperLink.getHref());
        assertEquals("only-item-title", hyperLink.getTitle());
        assertEquals("only-item-value", hyperLink.getName());
    }

    @Test
    public void testGetLinkWithMultipleResourcesArrayAndName() {
        ArrayNode linkArray = nodeFactory.arrayNode();
        ObjectNode link1 = nodeFactory.objectNode();
        ObjectNode link2 = nodeFactory.objectNode();
        link1.put("href", "/first-item");
        link1.put("title", "first-item-title");
        link1.put("name", "first-item-value");
        link2.put("href", "/second-item");
        link2.put("title", "second-item-title");
        link2.put("name", "second-item-value");
        linkArray.add(link1);
        linkArray.add(link2);

        links.put("bb:multi", linkArray);

        HalJsonResource resource = new HalJsonResource(root);
        HyperLink hyperLink = resource.getLink("bb:multi", "second-item-value");
        assertEquals("/second-item", hyperLink.getHref());
        assertEquals("second-item-title", hyperLink.getTitle());
        assertEquals("second-item-value", hyperLink.getName());
    }

    @Test(expected = HyperResourceException.class)
    public void testGetLinkWithMultipleResourcesArrayAndNameError() {
        ArrayNode linkArray = nodeFactory.arrayNode();
        ObjectNode link1 = nodeFactory.objectNode();
        ObjectNode link2 = nodeFactory.objectNode();
        link1.put("href", "/first-item");
        link1.put("title", "first-item-title");
        link1.put("name", "first-item-value");
        link2.put("href", "/second-item");
        link2.put("title", "second-item-title");
        link2.put("name", "second-item-value");
        linkArray.add(link1);
        linkArray.add(link2);

        links.put("bb:multi", linkArray);

        HalJsonResource resource = new HalJsonResource(root);
        resource.getLink("bb:multi", "unknown-item-value");
    }


    @Test
    public void testGetLinksNoLinksAtAll() {
        HalJsonResource resource = new HalJsonResource(root);
        assertArrayEquals(new HyperLink[0], resource.getLinks());

    }

    @Test
    public void testGetLinksNoLinksEntry() {
        root.remove("_links");
        HalJsonResource resource = new HalJsonResource(root);
        assertArrayEquals(new HyperLink[0], resource.getLinks());

    }

    @Test
    public void testGetLinksNoLinksInRel() {
        ArrayNode linkArray = nodeFactory.arrayNode();

        links.put("bb:multi", linkArray);

        HalJsonResource resource = new HalJsonResource(root);
        assertArrayEquals(new HyperLink[0], resource.getLinks());

    }

    @Test
    public void testGetLinksSingleAndMultipleLinks() {
        ArrayNode linkArray = nodeFactory.arrayNode();
        ObjectNode link1 = nodeFactory.objectNode();
        ObjectNode link2 = nodeFactory.objectNode();
        link1.put("href", "/first-item");
        link1.put("title", "first-item-title");
        link2.put("href", "/second-item");
        link2.put("title", "second-item-title");
        linkArray.add(link1);
        linkArray.add(link2);

        links.put("bb:multi", linkArray);

        ObjectNode singleLink = nodeFactory.objectNode();
        singleLink.put("href", "/singleItem");
        singleLink.put("title", "single-item-title");

        links.put("bb:single", singleLink);

        HalJsonResource resource = new HalJsonResource(root);
        HyperLink[] links = resource.getLinks();

        assertEquals(3, links.length);
        assertThat(links, arrayContainingInAnyOrder(
            allOf(
                hasProperty("title", equalTo("first-item-title")),
                hasProperty("rel", equalTo("bb:multi"))
            ),
            allOf(
                hasProperty("title", equalTo("second-item-title")),
                hasProperty("rel", equalTo("bb:multi"))
            ),
            allOf(
                hasProperty("title", equalTo("single-item-title")),
                hasProperty("rel", equalTo("bb:single"))
            )
        ));

    }

    @Test
    public void testGetLinksWithSingleResource() {
        String linkHref = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions";
        String title = "a title";

        ObjectNode promosLink = nodeFactory.objectNode();
        promosLink.put("href", linkHref);
        promosLink.put("title", title);
        links.put("bb:promotions", promosLink);

        HalJsonResource resource = new HalJsonResource(root);
        HyperLink[] links = resource.getLinks("bb:promotions");
        assertThat(links, is(not(nullValue())));
        assertThat(links.length, is(1));
        assertThat(links[0].getHref(), is(linkHref));
        assertThat(links[0].getTitle(), is(title));
    }

    @Test
    public void testGetLinksWithOneResource() {
        ArrayNode linkArray = nodeFactory.arrayNode();
        ObjectNode link = nodeFactory.objectNode();
        link.put("href", "/only-item");
        link.put("title", "only-item-title");
        linkArray.add(link);

        links.put("bb:one", linkArray);

        HalJsonResource resource = new HalJsonResource(root);
        HyperLink[] links = resource.getLinks("bb:one");
        assertThat(links, is(not(nullValue())));
        assertThat(links.length, is(1));
        assertThat(links[0].getHref(), is("/only-item"));
        assertThat(links[0].getTitle(), is("only-item-title"));
    }

    @Test
    public void testGetLinksWithMultipleResourcesArray() {
        ArrayNode linkArray = nodeFactory.arrayNode();
        ObjectNode link1 = nodeFactory.objectNode();
        ObjectNode link2 = nodeFactory.objectNode();
        link1.put("href", "/first-item");
        link1.put("title", "first-item-title");
        link2.put("href", "/second-item");
        link2.put("title", "second-item-title");
        linkArray.add(link1);
        linkArray.add(link2);

        links.put("bb:multi", linkArray);

        HalJsonResource resource = new HalJsonResource(root);
        HyperLink[] links = resource.getLinks("bb:multi");
        assertThat(links, is(not(nullValue())));
        assertThat(links.length, is(2));
        assertThat(links[0].getHref(), is("/first-item"));
        assertThat(links[0].getTitle(), is("first-item-title"));
        assertThat(links[1].getHref(), is("/second-item"));
        assertThat(links[1].getTitle(), is("second-item-title"));
    }

    @Test
    public void testGetLinksWithSingleResourceAndName() {
        String linkHref = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions";
        String title = "a title";
        String name = "a value";

        ObjectNode promosLink = nodeFactory.objectNode();
        promosLink.put("href", linkHref);
        promosLink.put("title", title);
        promosLink.put("name", name);
        links.put("bb:promotions", promosLink);

        HalJsonResource resource = new HalJsonResource(root);
        HyperLink[] links = resource.getLinks("bb:promotions", name);
        assertThat(links, is(not(nullValue())));
        assertThat(links.length, is(1));
        assertThat(links[0].getHref(), is(linkHref));
        assertThat(links[0].getTitle(), is(title));
        assertThat(links[0].getName(), is(name));
    }

    @Test
    public void testGetLinksWithOneResourceAndName() {
        ArrayNode linkArray = nodeFactory.arrayNode();
        ObjectNode link = nodeFactory.objectNode();
        link.put("href", "/only-item");
        link.put("title", "only-item-title");
        link.put("name", "only-item-value");
        linkArray.add(link);

        links.put("bb:one", linkArray);

        HalJsonResource resource = new HalJsonResource(root);
        HyperLink[] links = resource.getLinks("bb:one", "only-item-value");
        assertThat(links, is(not(nullValue())));
        assertThat(links.length, is(1));
        assertThat(links[0].getHref(), is("/only-item"));
        assertThat(links[0].getTitle(), is("only-item-title"));
        assertThat(links[0].getName(), is("only-item-value"));
    }

    @Test
    public void testGetLinksWithMultipleResourcesArrayAndName() {
        ArrayNode linkArray = nodeFactory.arrayNode();
        ObjectNode link1 = nodeFactory.objectNode();
        ObjectNode link2 = nodeFactory.objectNode();
        ObjectNode link3 = nodeFactory.objectNode();
        link1.put("href", "/first-item");
        link1.put("title", "first-item-title");
        link1.put("name", "first-item-value");
        link2.put("href", "/second-item");
        link2.put("title", "second-item-title");
        link2.put("name", "second-item-value");
        link3.put("href", "/third-item");
        link3.put("title", "third-item-title");
        link3.put("name", "second-item-value");
        linkArray.add(link1);
        linkArray.add(link2);
        linkArray.add(link3);

        links.put("bb:multi", linkArray);

        HalJsonResource resource = new HalJsonResource(root);
        HyperLink[] links = resource.getLinks("bb:multi", "second-item-value");
        assertThat(links, is(not(nullValue())));
        assertThat(links.length, is(2));
        assertThat(links[0].getHref(), is("/second-item"));
        assertThat(links[0].getTitle(), is("second-item-title"));
        assertThat(links[0].getName(), is("second-item-value"));
        assertThat(links[1].getHref(), is("/third-item"));
        assertThat(links[1].getTitle(), is("third-item-title"));
        assertThat(links[1].getName(), is("second-item-value"));
    }

    @Test
    public void testGetSimpleData() {
        String value = UUID.randomUUID().toString();

        root.put("somekey", value);

        HalJsonResource resource = new HalJsonResource(root);

        assertEquals(value, resource.getPathAs(String.class, "somekey"));

    }


    @Test
    public void testGetSimpleNestedData() {
        String value = UUID.randomUUID().toString();

        ObjectNode complexProp = nodeFactory.objectNode();

        complexProp.put("somekey", value);
        root.put("complexProp", complexProp);

        HalJsonResource resource = new HalJsonResource(root);

        assertEquals(value, resource.getPathAs(String.class, "complexProp", "somekey"));

    }


    //TOOD: HOW DO YOU RETRIEVE A COMPLEX PROPERTY?

    @Test(expected = HyperResourceException.class)
    public void testGetLinkMissingNode() {
        new HalJsonResource(root).getLink("bb:promotions");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLinkNullPath() {
        new HalJsonResource(root).getLink(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLinkEmptyRelationship() {

        new HalJsonResource(root).getLink("");
    }



    @Test(expected = HyperResourceException.class)
    public void testGetPathAsMissingNode() {
        new HalJsonResource(root).getPathAs(String.class, "_embedded", "promotionResourceList", "title");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPathAsNullPath() {
        new HalJsonResource(root).getPathAs(String.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPathAsNullType() {
        new HalJsonResource(root).getPathAs(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetValueEmptyPath() {
        new HalJsonResource(root).getPathAs(String.class);
    }


    @Test
    public void testEquals(){
        //two hal json resources are equal if their underlying json is equal
        HalJsonResource resource1 = new HalJsonResource(root);
        HalJsonResource resource2 = new HalJsonResource(root);

        assertEquals(resource1, resource2);

        HalJsonResource resource3 = new HalJsonResource(embedded);

        assertFalse(resource1.equals(resource3));

    }


    @Test
    public void testHasLink() {

        String relationship = "bb:promotions";

        //test it before the link is there
        HalJsonResource resource = new HalJsonResource(root);
        assertFalse(resource.hasLink(relationship));


        //Now put it in there and test
        ObjectNode promos = makeValidLinkNode();
        links.put(relationship, promos);
        resource = new HalJsonResource(root);
        assertTrue(resource.hasLink(relationship));

        //now we'll throw one in _embedded to to make sure that doesn't mess up anything
        embedded.put(relationship, nodeFactory.objectNode());
        resource = new HalJsonResource(root);
        assertTrue(resource.hasLink(relationship));

        //Now take it out of the _links, but leave in embedded and make sure it's there
        links.remove(relationship);
        resource = new HalJsonResource(root);
        assertTrue(resource.hasLink(relationship));

        //Take it out of embedded (back to start state) and make sure it's not there
        embedded.remove(relationship);
        resource = new HalJsonResource(root);
        assertFalse(resource.hasLink(relationship));
    }

    @Test
    public void testHasLinkWithName() {
        String relationship = "bb:promotions";
        String name = "name";

        //test it before the link is there
        HalJsonResource resource = new HalJsonResource(root);
        assertFalse(resource.hasLink(relationship, name));

        //Now put it in there and test
        ObjectNode promos = makeValidLinkNode();
        promos.put("name", name);
        links.put(relationship, promos);
        resource = new HalJsonResource(root);
        assertTrue(resource.hasLink(relationship, name));

        //now we'll throw one in _embedded to to make sure that doesn't mess up anything
        embedded.put(relationship, promos);
        resource = new HalJsonResource(root);
        assertTrue(resource.hasLink(relationship, name));

        //Now take it out of the _links, but leave in embedded and make sure it's there
        links.remove(relationship);
        resource = new HalJsonResource(root);
        assertFalse(resource.hasLink(relationship, name));

        //Take it out of embedded (back to start state) and make sure it's not there
        embedded.remove(relationship);
        resource = new HalJsonResource(root);
        assertFalse(resource.hasLink(relationship, name));
    }

    @Test
    public void testHasLinkArrayWithName() {
        String relationship = "bb:promotions";
        String name = "second-item-value";

        //test it before the link is there
        HalJsonResource resource = new HalJsonResource(root);
        assertFalse(resource.hasLink(relationship, name));

        //Now put it in there and test
        ArrayNode linkArray = nodeFactory.arrayNode();
        ObjectNode link1 = makeValidLinkNode();
        ObjectNode link2 = makeValidLinkNode();
        ObjectNode link3 = makeValidLinkNode();
        link1.put("name", "first-item-value");
        link2.put("name", name);
        link3.put("name", name);
        linkArray.add(link1);
        linkArray.add(link2);
        linkArray.add(link3);
        links.put(relationship, linkArray);
        resource = new HalJsonResource(root);
        assertTrue(resource.hasLink(relationship, name));

        //now we'll throw one in _embedded to to make sure that doesn't mess up anything
        embedded.put(relationship, linkArray);
        resource = new HalJsonResource(root);
        assertTrue(resource.hasLink(relationship, name));

        //Now take it out of the _links, but leave in embedded and make sure it's there
        links.remove(relationship);
        resource = new HalJsonResource(root);
        assertFalse(resource.hasLink(relationship, name));

        //Take it out of embedded (back to start state) and make sure it's not there
        embedded.remove(relationship);
        resource = new HalJsonResource(root);
        assertFalse(resource.hasLink(relationship, name));
    }


    @Test
    public void testCanResolveLinkLocal() {

        String relationship = "bb:promotions";

        ObjectNode promos = nodeFactory.objectNode();
        embedded.put(relationship, promos); //This is the key, the relationship in embedded
        ObjectNode promoLinks = nodeFactory.objectNode();

        promos.put("_links", promoLinks);
        ObjectNode promosSelfLink = nodeFactory.objectNode();
        promoLinks.put("self", promosSelfLink);

        HalJsonResource resource = new HalJsonResource(root);
        assertTrue(resource.canResolveLinkLocal(relationship));

        assertFalse(resource.canResolveLinkLocal("bb:some-link-not-there"));

        //remove embedded
        root.remove("_embedded");
        assertFalse(resource.canResolveLinkLocal(relationship));
    }

    @Test
    public void linkHasMultiLinks() {

        String relationship = "bb:promotions";
        //make it a multi link relationship
        ArrayNode promos = nodeFactory.arrayNode();

        //test it before the link is there
        HalJsonResource resource = new HalJsonResource(root);
        assertFalse(resource.hasLink(relationship));


        //Now put it in there and test
        links.put(relationship, promos);
        resource = new HalJsonResource(root);
        assertFalse("empty array should be false", resource.hasLink(relationship));

        //Now add a link object to the relationship
        ObjectNode link1 = nodeFactory.objectNode();
        link1.put("href", "/first-item");
        link1.put("title", "first-item-title");
        promos.add(link1);
        resource = new HalJsonResource(root);
        assertTrue("array with 1 entry should be true", resource.hasLink(relationship));

        //Add in another
        ObjectNode link2 = nodeFactory.objectNode();
        link2.put("href", "/second-item");
        link2.put("title", "second-item-title");
        promos.add(link2);
        resource = new HalJsonResource(root);
        assertTrue("array with 2 entry should be true", resource.hasLink(relationship));

        //now we'll throw one in _embedded to to make sure that doesn't mess up anything
        ArrayNode embeddedPromos = nodeFactory.arrayNode();
        embedded.put(relationship, embeddedPromos);
        resource = new HalJsonResource(root);
        assertTrue(resource.hasLink(relationship));

        //Now take it out of the _links, but leave in embedded and make sure it's there
        links.remove(relationship);
        resource = new HalJsonResource(root);
        assertTrue(resource.hasLink(relationship));

        //A weird edge case where an entry is present in links, but is an empty array.  Also entry is in _embedded
        links.put(relationship, nodeFactory.arrayNode());
        assertTrue("because it's embedded it should be true", resource.hasLink(relationship));

        //Take it out of embedded (back to start state) and make sure it's not there
        embedded.remove(relationship);
        resource = new HalJsonResource(root);
        assertFalse(resource.hasLink(relationship));


    }

    @Test
    public void linkWithEmptyArray() {
        ArrayNode linkArray = nodeFactory.arrayNode();
        linkArray.addAll(Collections.EMPTY_LIST);

        HalJsonResource resource = new HalJsonResource(root);
        assertThat(resource.hasLink("bb:multi"), is(false));
    }


    @Test
    public void testResolveLinkLocalSingleResource() {

        String relationship = "bb:promotions";


        ObjectNode promos = nodeFactory.objectNode();
        embedded.put(relationship, promos);
        ObjectNode promoLinks = nodeFactory.objectNode();

        promos.put("_links", promoLinks);
        ObjectNode promosSelfLink = nodeFactory.objectNode();
        promoLinks.put("self", promosSelfLink);

        promos.put("some thing", "some value");


        HalJsonResource resource = new HalJsonResource(root);
        HyperResource actual = resource.resolveLinkLocal(relationship);

        HalJsonResource expected = new HalJsonResource(promos);

        assertEquals(expected, actual);
    }


    @Test
    public void testResolveLinkLocalMultipleResources() {

        String relationship = "bb:promotions";

        ArrayNode promos = nodeFactory.arrayNode();
        embedded.put(relationship, promos);


        ObjectNode promo1 = nodeFactory.objectNode();
        ObjectNode promo1Links = nodeFactory.objectNode();
        promo1.put("_links", promo1Links);
        ObjectNode promo1SelfLink = nodeFactory.objectNode();
        promo1Links.put("self", promo1SelfLink);

        String value1 = UUID.randomUUID().toString();
        promo1.put("some thing", value1);

        promos.add(promo1);


        ObjectNode promo2 = nodeFactory.objectNode();
        ObjectNode promo2Links = nodeFactory.objectNode();
        promo2.put("_links", promo2Links);
        ObjectNode promo2SelfLink = nodeFactory.objectNode();
        promo1Links.put("self", promo2SelfLink);

        String value2 = UUID.randomUUID().toString();
        promo1.put("some thing", value2);

        promos.add(promo2);

        HalJsonResource resource = new HalJsonResource(root);
        HyperResource actual = resource.resolveLinkLocal(relationship);

        HalJsonResource expected = new HalJsonResource(promos);

        assertEquals(expected, actual);
    }


    @Test
    public void testResolveLinkLocal0Resources() {

        String relationship = "bb:promotions";

        ArrayNode promos = nodeFactory.arrayNode();
        embedded.put(relationship, promos);

        //Note it's an empty array!

        HalJsonResource resource = new HalJsonResource(root);
        HyperResource actual = resource.resolveLinkLocal(relationship);

        HyperResource expected = new HalJsonResource(promos);
        assertEquals(expected, actual);
    }


    @Test
    public void testResolveLinkLocalResourceNotPresent(){
        String relationship = "bb:promotions";
        String linkHref = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions";

        //Put the relationship in the _links just for kicks
        ObjectNode promosLink = nodeFactory.objectNode();
        links.put(relationship, promosLink);
        promosLink.put("href", linkHref);


        HalJsonResource resource = new HalJsonResource(root);
        try{
            resource.resolveLinkLocal(relationship);
            fail("expected exception not thrown");
        } catch (HyperClientException e ){
            //TODO: what should we test for?
        }
    }


    @Test
    public void testResolveLinkLocalEmbeddedNotPresent(){
        String relationship = "bb:promotions";
        String linkHref = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions";

        //Put the relationship in the _links just for kicks
        ObjectNode promosLink = nodeFactory.objectNode();
        links.put(relationship, promosLink);
        promosLink.put("href", linkHref);

        //remove the _embedded
        root.remove("_embedded");

        HalJsonResource resource = new HalJsonResource(root);
        try {
            resource.resolveLinkLocal(relationship);
            fail("expected exception not thrown");
        } catch (HyperClientException e ) {
            //TODO: what should we test for?
        }
    }


    @Test
    public void testGetProfilesWithArrayOfOneProfile(){
        String relationship = "profile";
        String linkHref1 = "http://host/profiles/promotions";

        ArrayNode profileLinks = nodeFactory.arrayNode();
        links.put(relationship, profileLinks);

        ObjectNode link1 = nodeFactory.objectNode();
        link1.put("href", linkHref1);

        profileLinks.add(link1);

        //remove the _embedded
        root.remove("_embedded");

        HalJsonResource resource = new HalJsonResource(root);
        LinkedHashSet<String> actual = resource.getProfiles();

        assertEquals(makeSet(linkHref1), actual);

    }

    @Test
    public void testGetProfilesWithArrayOfTwoProfile(){
        String relationship = "profile";
        String linkHref1 = "http://host/profiles/promotions-subtype";
        String linkHref2 = "http://host/profiles/promotions";

        ArrayNode profileLinks = nodeFactory.arrayNode();
        links.put(relationship, profileLinks);

        ObjectNode link1 = nodeFactory.objectNode();
        link1.put("href", linkHref1);

        ObjectNode link2 = nodeFactory.objectNode();
        link2.put("href", linkHref2);

        profileLinks.add(link1);
        profileLinks.add(link2);

        //remove the _embedded
        root.remove("_embedded");

        HalJsonResource resource = new HalJsonResource(root);
        LinkedHashSet<String> actual = resource.getProfiles();

        assertEquals(makeSet(linkHref1, linkHref2), actual);


    }

    @Test
    public void testGetProfilesWithSingleProfileLink(){
        String relationship = "profile";
        String linkHref = "http://host/profiles/promotions-subtype";

        ObjectNode profileLink = nodeFactory.objectNode();
        links.put(relationship, profileLink);
        profileLink.put("href", linkHref);

        //remove the _embedded
        root.remove("_embedded");

        HalJsonResource resource = new HalJsonResource(root);
        LinkedHashSet<String> actual = resource.getProfiles();

        assertEquals(makeSet(linkHref), actual);


    }
}
