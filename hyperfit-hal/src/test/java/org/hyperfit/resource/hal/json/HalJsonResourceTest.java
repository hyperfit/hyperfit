package org.hyperfit.resource.hal.json;


import org.hamcrest.Matchers;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.net.Request;
import org.hyperfit.net.Response;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.HyperResourceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import static org.mockito.Mockito.when;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static test.Helpers.random;
import static test.Helpers.uniqueString;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hyperfit.resource.controls.form.Form;
import org.hyperfit.resource.controls.link.HyperLink;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;


public class HalJsonResourceTest {

    static JsonNodeFactory nodeFactory = new JsonNodeFactory(false);
    ObjectNode root;
    ObjectNode links;
    ObjectNode embedded;

    @Mock
    Response mockResponse;

    @Mock
    Request mockRequest;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        root = nodeFactory.objectNode();
        links = nodeFactory.objectNode();
        embedded = nodeFactory.objectNode();
        root.put("_links", links);
        root.put("_embedded", embedded);
    }

    public static ObjectNode makeValidLinkNode(){
        ObjectNode linkNode = nodeFactory.objectNode();
        linkNode.put("href", "http://url/to/" + uniqueString());

        return linkNode;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDataSourceNull() {
        new HalJsonResource((JsonNode)null, null);
    }

    @Test
    public void testGetLinkWithSingleResource() {
        String linkHref = "http://api-cloud-01.qa:8080/commerce-hyper-api/promotions";
        String title = "a title";

        ObjectNode promosLink = nodeFactory.objectNode();
        links.put("bb:promotions", promosLink);
        promosLink.put("href", linkHref);
        promosLink.put("title", title);

        HalJsonResource resource = new HalJsonResource(root, null);
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

        HalJsonResource resource = new HalJsonResource(root, null);
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

        HalJsonResource resource = new HalJsonResource(root, null);
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

        HalJsonResource resource = new HalJsonResource(root, null);
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

        HalJsonResource resource = new HalJsonResource(root, null);
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

        HalJsonResource resource = new HalJsonResource(root, null);
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

        HalJsonResource resource = new HalJsonResource(root, null);
        resource.getLink("bb:multi", "unknown-item-value");
    }


    @Test
    public void testGetLinksNoLinksAtAll() {
        HalJsonResource resource = new HalJsonResource(root, null);
        assertArrayEquals(new HyperLink[0], resource.getLinks());

    }

    @Test
    public void testGetLinksNoLinksEntry() {
        root.remove("_links");
        HalJsonResource resource = new HalJsonResource(root, null);
        assertArrayEquals(new HyperLink[0], resource.getLinks());

    }

    @Test
    public void testGetLinksNoLinksInRel() {
        ArrayNode linkArray = nodeFactory.arrayNode();

        links.put("bb:multi", linkArray);

        HalJsonResource resource = new HalJsonResource(root, null);
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

        HalJsonResource resource = new HalJsonResource(root, null);
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

        HalJsonResource resource = new HalJsonResource(root, null);
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

        HalJsonResource resource = new HalJsonResource(root, null);
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

        HalJsonResource resource = new HalJsonResource(root, null);
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

        HalJsonResource resource = new HalJsonResource(root, null);
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

        HalJsonResource resource = new HalJsonResource(root, null);
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

        HalJsonResource resource = new HalJsonResource(root, null);
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
        String stringValue = uniqueString();
        boolean boolValue = random(true, false);
        double doubleValue = Math.random();

        root.put("string", stringValue)
            .put("bool", boolValue)
            .put("double", doubleValue)
            .put("int", -1)
        ;


        HalJsonResource resource = new HalJsonResource(root, null);

        assertEquals(stringValue, resource.getPathAs(String.class, "string"));

        //wrapped
        assertEquals(boolValue, resource.getPathAs(Boolean.class, "bool"));
        assertEquals(doubleValue, resource.getPathAs(Double.class, "double"), 0d);
        assertEquals(-1, (int)resource.getPathAs(Integer.class, "int"));

        //primitive
        assertEquals(boolValue, resource.getPathAs(boolean.class, "bool"));
        assertEquals(doubleValue, resource.getPathAs(double.class, "double"), 0d);
        assertEquals(-1, (int)resource.getPathAs(int.class, "int"));

    }


    @Test
    public void testGetSimpleDataNullValues() {


        root.put("string", (String)null)
            .put("bool", (Boolean)null)
            .put("double", (Double)null)
            .put("int", (Integer)null)
        ;


        HalJsonResource resource = new HalJsonResource(root, null);

        assertNull(resource.getPathAs(String.class, "string"));

        //wrapped
        assertNull(resource.getPathAs(Boolean.class, "bool"));
        assertNull(resource.getPathAs(Double.class, "double"));
        assertNull(resource.getPathAs(Integer.class, "int"));

        //primitive
        assertFalse(resource.getPathAs(boolean.class, "bool"));
        assertEquals(0.0, resource.getPathAs(double.class, "double"), 0d);
        assertEquals(0, (int)resource.getPathAs(int.class, "int"));

    }

    static class POJO {
        private String someString;
        private Double someDouble;

        public String getSomeString() {
            return someString;
        }

        public Double getSomeDouble() {
            return someDouble;
        }
    }

    @Test
    public void testGetPOJO() {

        ObjectNode complexPropNode = nodeFactory.objectNode();

        String stringValue = uniqueString();
        complexPropNode.put("someString", stringValue);

        Double doubleValue =  Math.random();
        complexPropNode.put("someDouble", doubleValue);


        root.put("somekey", complexPropNode);

        HalJsonResource resource = new HalJsonResource(root, null);

        POJO actual = resource.getPathAs(POJO.class, "somekey");

        assertEquals(stringValue, actual.getSomeString());
        assertEquals(doubleValue, actual.getSomeDouble());

    }

    @Test
    public void testGetPOJOExtraProps() {

        ObjectNode complexPropNode = nodeFactory.objectNode();

        String stringValue = uniqueString();
        complexPropNode.put("someString", stringValue);

        Double doubleValue =  Math.random();
        complexPropNode.put("someDouble", doubleValue);

        complexPropNode.put(uniqueString(), uniqueString());


        root.put("somekey", complexPropNode);

        HalJsonResource resource = new HalJsonResource(root, null);

        POJO actual = resource.getPathAs(POJO.class, "somekey");

        assertEquals(stringValue, actual.getSomeString());
        assertEquals(doubleValue, actual.getSomeDouble());

    }


    @Test
    public void testGetSimpleNestedData() {
        String value = uniqueString();

        ObjectNode complexProp = nodeFactory.objectNode();

        complexProp.put("somekey", value);
        root.put("complexProp", complexProp);

        HalJsonResource resource = new HalJsonResource(root, null);

        assertEquals(value, resource.getPathAs(String.class, "complexProp", "somekey"));

    }


    @Test
    public void testGetEmbeddedAsJsonNode() {
        String value = uniqueString();

        ObjectNode subResource = nodeFactory.objectNode();

        subResource.put("somekey", value);
        embedded.put("bb:resource", subResource);

        ObjectNode complexProp = nodeFactory.objectNode();
        complexProp.put("propKey", value);
        subResource.put("complexProp", complexProp);

        HalJsonResource resource = new HalJsonResource(root, null);



        ObjectNode expected = nodeFactory.objectNode();
        expected.put("somekey", value);
        expected.put("complexProp", complexProp);

        assertEquals(expected, resource.getPathAs(JsonNode.class, "_embedded", "bb:resource"));

    }



    @Test
    public void testGetEmbeddedAsMap() {
        final String value = uniqueString();

        ObjectNode subResource = nodeFactory.objectNode();

        subResource.put("somekey", value);
        embedded.put("bb:resource", subResource);

        ObjectNode complexProp = nodeFactory.objectNode();
        complexProp.put("propKey", value);
        subResource.put("complexProp", complexProp);

        HalJsonResource resource = new HalJsonResource(root, null);

        LinkedHashMap<String,Object> expected = new LinkedHashMap<String, Object>(){{
            put("somekey", value);
            put("complexProp", new HashMap<String, Object>(){{
                put("propKey", value);
            }});
        }};


        assertEquals(expected, resource.getPathAs(Map.class, "_embedded", "bb:resource"));

    }


    //TOOD: HOW DO YOU RETRIEVE A COMPLEX PROPERTY?

    @Test(expected = HyperResourceException.class)
    public void testGetLinkMissingNode() {
        new HalJsonResource(root, null).getLink("bb:promotions");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLinkNullPath() {
        new HalJsonResource(root, null).getLink(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLinkEmptyRelationship() {

        new HalJsonResource(root, null).getLink("");
    }

    @Test
    public void testGetDataFieldNamesOnlyHALControlFields() {
        assertThat(
            new HalJsonResource(root, null).getDataFieldNames(),
            Matchers.emptyArray()
        );

    }

    @Test
    public void testGetDataFieldNamesOnlyHALReservedFields() {
        root.put(
            "_reservedCauseStartsWithUnderbar",
            uniqueString()
        );


        assertThat(
            "reserved _ prefixed names are ignored",
            new HalJsonResource(root, null).getDataFieldNames(),
            Matchers.emptyArray()
        );

    }

    @Test
    public void testGetDataFieldNamesOnlyHALReservedFieldsWithWhitelist() {
        root.put(
            "_reservedCauseStartsWithUnderbar",
            uniqueString()
        );

        //this one is whitelisted
        root.put(
            "_id",
            uniqueString()
        );


        assertThat(
            "reserved _ prefixed names are ignored unless they are whitelisted",
            new HalJsonResource(root, null).getDataFieldNames(),
            arrayContainingInAnyOrder(
                "_id"
            )
        );

    }

    @Test
    public void testGetDataFieldNamesWithComplexProp() {

        String complexKey = "complexProp";

        root.put(
            complexKey,
            nodeFactory.objectNode().put(
                uniqueString(),
                uniqueString()
            )
        );

        root.put(
            "_reservedCauseStartsWithUnderbar",
            uniqueString()
        );


        String otherKey = uniqueString();

        root.put(
            otherKey,
            uniqueString()
        );


        assertThat(
            new HalJsonResource(root, null).getDataFieldNames(),
            arrayContainingInAnyOrder(
                complexKey,
                otherKey
            )
        );

    }

    @Test
    public void testHasPath() {
        assertFalse(new HalJsonResource(root, null).hasPath("_embedded", "promotionResourceList", "title"));
        assertFalse(new HalJsonResource(root, null).hasPath());

        String value = uniqueString();

        root.put("complexProp", nodeFactory.objectNode().put("somekey", value));

        HalJsonResource resource = new HalJsonResource(root, null);

        assertTrue(value, resource.hasPath("complexProp", "somekey"));

    }

    @Test(expected = HyperResourceException.class)
    public void testGetPathAsMissingNode() {
        new HalJsonResource(root, null).getPathAs(String.class, "_embedded", "promotionResourceList", "title");
    }

    @Test(expected = HyperResourceException.class)
    public void testGetPathAsMissingNodeWithFalseNullWhenMissing() {
        new HalJsonResource(root, null).getPathAs(String.class, false, "_embedded", "promotionResourceList", "title");
    }


    public void testGetPathAsMissingNodeWithTrueNullWhenMissing() {
        assertNull(new HalJsonResource(root, null).getPathAs(String.class, true, "_embedded", "promotionResourceList", "title"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPathAsNullPath() {
        new HalJsonResource(root, null).getPathAs(String.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPathAsNullType() {
        new HalJsonResource(root, null).getPathAs(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetValueEmptyPath() {
        new HalJsonResource(root, null).getPathAs(String.class);
    }


    @Test
    public void testEquals(){
        //two hal json resources are equal if their underlying json is equal
        HalJsonResource resource1 = new HalJsonResource(root, null);
        HalJsonResource resource2 = new HalJsonResource(root, null);

        assertEquals(resource1, resource2);

        HalJsonResource resource3 = new HalJsonResource(embedded, null);

        assertNotEquals(resource1, resource3);

    }


    @Test
    public void testHasLink() {

        String relationship = "bb:promotions";

        //test it before the link is there
        HalJsonResource resource = new HalJsonResource(root, null);
        assertFalse(resource.hasLink(relationship));


        //Now put it in there and test
        ObjectNode promos = makeValidLinkNode();
        links.put(relationship, promos);
        resource = new HalJsonResource(root, null);
        assertTrue(resource.hasLink(relationship));

        //now we'll throw one in _embedded to to make sure that doesn't mess up anything
        embedded.put(relationship, nodeFactory.objectNode());
        resource = new HalJsonResource(root, null);
        assertTrue(resource.hasLink(relationship));

        //Now take it out of the _links, but leave in embedded and make sure it's there
        links.remove(relationship);
        resource = new HalJsonResource(root, null);
        assertTrue(resource.hasLink(relationship));

        //Take it out of embedded (back to start state) and make sure it's not there
        embedded.remove(relationship);
        resource = new HalJsonResource(root, null);
        assertFalse(resource.hasLink(relationship));
    }

    @Test
    public void testHasLinkWithName() {
        String relationship = "bb:promotions";
        String name = "name";

        //test it before the link is there
        HalJsonResource resource = new HalJsonResource(root, null);
        assertFalse(resource.hasLink(relationship, name));

        //Now put it in there and test
        ObjectNode promos = makeValidLinkNode();
        promos.put("name", name);
        links.put(relationship, promos);
        resource = new HalJsonResource(root, null);
        assertTrue(resource.hasLink(relationship, name));

        //now we'll throw one in _embedded to to make sure that doesn't mess up anything
        embedded.put(relationship, promos);
        resource = new HalJsonResource(root, null);
        assertTrue(resource.hasLink(relationship, name));

        //Now take it out of the _links, but leave in embedded and make sure it's there
        links.remove(relationship);
        resource = new HalJsonResource(root, null);
        assertFalse(resource.hasLink(relationship, name));

        //Take it out of embedded (back to start state) and make sure it's not there
        embedded.remove(relationship);
        resource = new HalJsonResource(root, null);
        assertFalse(resource.hasLink(relationship, name));
    }

    @Test
    public void testHasLinkArrayWithName() {
        String relationship = "bb:promotions";
        String name = "second-item-value";

        //test it before the link is there
        HalJsonResource resource = new HalJsonResource(root, null);
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
        resource = new HalJsonResource(root, null);
        assertTrue(resource.hasLink(relationship, name));

        //now we'll throw one in _embedded to to make sure that doesn't mess up anything
        embedded.put(relationship, linkArray);
        resource = new HalJsonResource(root, null);
        assertTrue(resource.hasLink(relationship, name));

        //Now take it out of the _links, but leave in embedded and make sure it's there
        links.remove(relationship);
        resource = new HalJsonResource(root, null);
        assertFalse(resource.hasLink(relationship, name));

        //Take it out of embedded (back to start state) and make sure it's not there
        embedded.remove(relationship);
        resource = new HalJsonResource(root, null);
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

        HalJsonResource resource = new HalJsonResource(root, null);
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
        HalJsonResource resource = new HalJsonResource(root, null);
        assertFalse(resource.hasLink(relationship));


        //Now put it in there and test
        links.put(relationship, promos);
        resource = new HalJsonResource(root, null);
        assertFalse("empty array should be false", resource.hasLink(relationship));

        //Now add a link object to the relationship
        ObjectNode link1 = nodeFactory.objectNode();
        link1.put("href", "/first-item");
        link1.put("title", "first-item-title");
        promos.add(link1);
        resource = new HalJsonResource(root, null);
        assertTrue("array with 1 entry should be true", resource.hasLink(relationship));

        //Add in another
        ObjectNode link2 = nodeFactory.objectNode();
        link2.put("href", "/second-item");
        link2.put("title", "second-item-title");
        promos.add(link2);
        resource = new HalJsonResource(root, null);
        assertTrue("array with 2 entry should be true", resource.hasLink(relationship));

        //now we'll throw one in _embedded to to make sure that doesn't mess up anything
        ArrayNode embeddedPromos = nodeFactory.arrayNode();
        embedded.put(relationship, embeddedPromos);
        resource = new HalJsonResource(root, null);
        assertTrue(resource.hasLink(relationship));

        //Now take it out of the _links, but leave in embedded and make sure it's there
        links.remove(relationship);
        resource = new HalJsonResource(root, null);
        assertTrue(resource.hasLink(relationship));

        //A weird edge case where an entry is present in links, but is an empty array.  Also entry is in _embedded
        links.put(relationship, nodeFactory.arrayNode());
        assertTrue("because it's embedded it should be true", resource.hasLink(relationship));

        //Take it out of embedded (back to start state) and make sure it's not there
        embedded.remove(relationship);
        resource = new HalJsonResource(root, null);
        assertFalse(resource.hasLink(relationship));


    }

    @Test
    public void linkWithEmptyArray() {
        ArrayNode linkArray = nodeFactory.arrayNode();
        linkArray.addAll(Collections.<JsonNode>emptyList());

        HalJsonResource resource = new HalJsonResource(root, null);
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


        HalJsonResource resource = new HalJsonResource(root, null);
        HyperResource actual = resource.resolveLinkLocal(relationship);

        HalJsonResource expected = new HalJsonResource(promos, null);

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

        String value1 = uniqueString();
        promo1.put("some thing", value1);

        promos.add(promo1);


        ObjectNode promo2 = nodeFactory.objectNode();
        ObjectNode promo2Links = nodeFactory.objectNode();
        promo2.put("_links", promo2Links);
        ObjectNode promo2SelfLink = nodeFactory.objectNode();
        promo1Links.put("self", promo2SelfLink);

        String value2 = uniqueString();
        promo1.put("some thing", value2);

        promos.add(promo2);

        HalJsonResource resource = new HalJsonResource(root, null);
        HyperResource actual = resource.resolveLinkLocal(relationship);

        HalJsonResource expected = new HalJsonResource(promos, null);

        assertEquals(expected, actual);
    }


    @Test
    public void testResolveLinkLocal0Resources() {

        String relationship = "bb:promotions";

        ArrayNode promos = nodeFactory.arrayNode();
        embedded.put(relationship, promos);

        //Note it's an empty array!

        HalJsonResource resource = new HalJsonResource(root, null);
        HyperResource actual = resource.resolveLinkLocal(relationship);

        HyperResource expected = new HalJsonResource(promos, null);
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


        HalJsonResource resource = new HalJsonResource(root, null);
        try{
            resource.resolveLinkLocal(relationship);
            fail("expected exception not thrown");
        } catch (HyperfitException e ){
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

        HalJsonResource resource = new HalJsonResource(root, null);
        try {
            resource.resolveLinkLocal(relationship);
            fail("expected exception not thrown");
        } catch (HyperfitException e ) {
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

        HalJsonResource resource = new HalJsonResource(root, null);

        assertThat(
            resource.getProfiles(),
            contains(
                linkHref1
            )
        );

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

        HalJsonResource resource = new HalJsonResource(root, null);
        LinkedHashSet<String> actual = resource.getProfiles();


        assertThat(
            resource.getProfiles(),
            contains(
                linkHref1,
                linkHref2
            )
        );


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

        HalJsonResource resource = new HalJsonResource(root, null);
        LinkedHashSet<String> actual = resource.getProfiles();


        assertThat(
            resource.getProfiles(),
            contains(
                linkHref
            )
        );


    }

    @Test
    public void testGetForms(){
        HalJsonResource resource = new HalJsonResource(root, null);
        Form[] actual = resource.getForms();

        assertArrayEquals("HAL doesn't support forms so it should always be empty", new Form[0], actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetForm(){
        //"HAL doesn't support forms so it should always throw"
        HalJsonResource resource = new HalJsonResource(root, null);
        resource.getForm(null);
    }

    @Test
    public void testHasForm(){
        HalJsonResource resource = new HalJsonResource(root, null);

        assertFalse("HAL doesn't support forms so it should always be empty", resource.hasForm(null));

    }


    @Test
    public void testRelativeURLs(){

        String requestURL = "proto://host.net:70/stuff/com";
        String json = "{" +
            "\"_links\" : {" +
                "\"root-relative\" : { \"href\" : \"/root-relative{?params}\"}," +
        //TODO: support other types of relative paths
               // "\"path-relative\" : { \"href\" : \"path/relative{?params}\"}," +
               // "\"path-relative-parent\" : { \"href\" : \"../parent/path{?params}\"}," +
               // "\"scheme-relative\" : { \"href\" : \"//host2.net/path{?params}\"}," +
                "\"absolute\" : { \"href\" : \"proto://host/path{?params}\"}" +
            "" +
            "}" +
        "}";
        when(mockResponse.getBody())
            .thenReturn(json);

        when(mockResponse.getRequest())
            .thenReturn(mockRequest);

        when(mockRequest.getUrl())
            .thenReturn(requestURL);

        HalJsonResource resource = new HalJsonResource(mockResponse);



        assertEquals("proto://host.net:70/root-relative{?params}", resource.getLink("root-relative").getHref());
        //assertEquals("proto://host.net:70/stuff/path/relative", resource.getLink("path-relative").getHref());
        //assertEquals("proto://host.net:70/parent/path", resource.getLink("path-relative-parent").getHref());
        //assertEquals("proto://host.net:70/parent/path", resource.getLink("scheme-relative").getHref());
        assertEquals("proto://host/path{?params}", resource.getLink("absolute").getHref());


    }
}
