package org.hyperfit;


import org.hyperfit.HyperRequestProcessor;
import org.hyperfit.HyperResourceInvokeHandler;
import org.hyperfit.RootResourceBuilder;
import org.hyperfit.annotation.Data;
import org.hyperfit.annotation.Link;
import org.hyperfit.annotation.Profiles;
import org.hyperfit.exception.ServiceException;
import org.hyperfit.http.HttpHeader;
import org.hyperfit.http.Method;
import org.hyperfit.http.Request;
import org.hyperfit.http.Response;
import org.hyperfit.methodinfo.ConcurrentHashMapResourceMethodInfoCache;
import org.hyperfit.methodinfo.ResourceMethodInfoCache;
import org.hyperfit.resource.HyperLink;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.HyperResourceException;
import org.hyperfit.resource.hal.json.HalJsonResource;
import org.hyperfit.resource.registry.ProfileResourceRegistryIndexStrategy;
import org.hyperfit.resource.registry.ResourceRegistry;
import org.hyperfit.utils.TypeInfo;
import org.hyperfit.utils.TypeRef;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.reflect.ClassPath;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class HyperResourceInvokeHandlerTest{

    public class ComplexProperty {
        private String dataAsString1;
        private String dataAsString2;

        public String dataAsString1() {
            return dataAsString1;
        }

        public String dataAsString2() {
            return dataAsString2;
        }
    }

    public interface DataResource extends HyperResource {

        @Data({"dataString"})
        public String dataAsString();

        @Data({"dataIntegerWrapper"})
        public Integer dataAsIntegerWrapper();

        @Data({"dataIntegerPrimitive"})
        public int dataAsIntegerPrimitive();

        @Data({"dataFloatWrapper"})
        public Float dataAsFloatWrapper();

        @Data({"dataFloatPrimitive"})
        public float dataAsFloatPrimitive();

        @Data({"dataLongWrapper"})
        public Long dataAsLongWrapper();

        @Data({"dataLongPrimitive"})
        public long dataAsLongPrimitive();

        @Data({"dataDoubleWrapper"})
        public Double dataAsDoubleWrapper();

        @Data({"dataDoublePrimitive"})
        public double dataAsDoublePrimitive();

        @Data({"dataShortWrapper"})
        public Short dataAsShortWrapper();

        @Data({"dataShortPrimitive"})
        public short dataAsShortPrimitive();

        @Data({"dataCharacterWrapper"})
        public Character dataAsCharacterWrapper();

        @Data({"dataCharacterPrimitive"})
        public char dataAsCharacterPrimitive();

        @Data({"dataBooleanWrapper"})
        public Boolean dataAsBooleanWrapper();

        @Data({"dataBooleanPrimitive"})
        public boolean dataAsBooleanPrimitive();

        @Data({"dataDateWrapper"})
        public Date dataAsDateWrapper();

        @Data({"dataDateWrapper", "missingNode"})
        public String missingNodeError();

        @Data("dataResource")
        public ComplexProperty dataAsResource();
    }


    public interface LinkResource extends HyperResource {
        @Link("bb:data")
        DataResource dataResource();

        @Link("bb:datas")
        DataResource[] dataResourcesArray();

        @Link("bb:datas")
        List<DataResource> dataResourcesList();

        @Link("bb:datas")
        List<RandomResource> multiLinkResources();

        @Link("bb:linkstring")
        String linkString();

        @Link("bb:linkHyperResponse")
        Response linkHyperResponse();

        @Link("bb:hyperLink")
        HyperLink hyperLink();

        @Link("bb:hyperLinks")
        HyperLink[] hyperLinks();

        @Link(value="bb:data", name="the_name")
        HyperLink dataLink();

        @Link("bb:profile-resources")
        public BaseProfileResource[] profileResources();

        @Link("bb:profile-resource-1")
        public BaseProfileResource profileResource1();

        @Link("bb:multiple-profile-resource")
        public BaseProfileResource multipleProfileResource();

        @Link("bb:not-in-registry-resource")
        public BaseProfileResource notInRegistryResource();
    }

    public interface RandomResource {

        @Data("randomString")
        String getRandomString();
    }


    public interface BaseProfileResource extends HyperResource {
    }

    @Profiles("/a/b/c/profile-resource-1")
    public interface ProfileResource1 extends BaseProfileResource {
    }

    @Profiles("/a/b/c/profile-resource-2")
    public interface ProfileResource2 extends BaseProfileResource {
    }

    @Profiles({"/a/b/c/multiple-profile-resource-a", "/a/b/c/multiple-profile-resource-b"})
    public interface MultipleProfileResource extends BaseProfileResource {
    }

    @Profiles("/a/b/c/not-in-registry-resource")
    public interface NotInRegistryResource extends BaseProfileResource {
    }



    @Mock
    private HyperResource mockHyperResource;

    @Mock
    private HyperRequestProcessor requestProcessor;

    private ResourceMethodInfoCache resourceMethodInfoCache;

    private ResourceRegistry resourceRegistry = new ResourceRegistry(new ProfileResourceRegistryIndexStrategy())
            .add(Arrays.asList((Class<? extends HyperResource>[]) new Class[]{
                    ProfileResource1.class, ProfileResource2.class, MultipleProfileResource.class}));

    private JsonNodeFactory nodeFactory = new JsonNodeFactory(false);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        resourceMethodInfoCache = new ConcurrentHashMapResourceMethodInfoCache();
    }

    /**
     * Returns a resource proxy of type T with the resource and client mocked
     *
     * @param clazz
     * @param hyperResource
     * @param <T>
     * @return
     */
    public <T> T getHyperResourceProxy(Class<T> clazz, HyperResource hyperResource, HyperRequestProcessor hyperResponseProcessor,
                                       TypeInfo typeInfo) {
        return clazz.cast(
                Proxy.newProxyInstance(
                        clazz.getClassLoader(),
                        new Class[]{clazz},
                        new HyperResourceInvokeHandler(hyperResource, hyperResponseProcessor, resourceMethodInfoCache.get(clazz), typeInfo)
                )
        );
    }

    /**
     * Returns a resource proxy of type T with the resource and client mocked
     *
     * @param clazz
     * @param hyperResource
     * @param <T>
     * @return
     */
    public <T> T getHyperResourceProxy(Class<T> clazz, HyperResource hyperResource, HyperRequestProcessor hyperResponseProcessor) {
        return getHyperResourceProxy(clazz, hyperResource, hyperResponseProcessor, null);
    }

    public <T> T getHyperResourceProxy(Class<T> clazz, HyperResource hyperResource) {

        return getHyperResourceProxy(clazz, hyperResource, requestProcessor);
    }

    @Test
    public void testInvokeToString() throws Exception {
        when(mockHyperResource.toString()).thenReturn("test");
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals("test", proxyTest.toString());
    }

    @Test
    public void testInvokeHashCode() throws Exception {

        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals(proxyTest.hashCode(), mockHyperResource.hashCode());
    }

    @Test
    public void testInvokeEquals() throws Exception {

        DataResource proxyTest1 = getHyperResourceProxy(DataResource.class, mockHyperResource);
        DataResource proxyTest2 = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertTrue(proxyTest1.equals(proxyTest2));
    }

    @Test
    public void testInvokeHyperResourceMethods() throws Exception {

        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);

        HyperLink expectedLink = createHyperLink();

        String fakeRelationship = UUID.randomUUID().toString();
        when(mockHyperResource.getLink(fakeRelationship)).thenReturn(expectedLink);
        assertEquals(expectedLink, proxyTest.getLink(fakeRelationship));


        when(mockHyperResource.canResolveLinkLocal(fakeRelationship)).thenReturn(false);
        assertFalse(proxyTest.canResolveLinkLocal(fakeRelationship));

        when(mockHyperResource.hasLink(fakeRelationship)).thenReturn(false);
        assertFalse(proxyTest.hasLink(fakeRelationship));

        HyperResource fakeEmbeddedResource = mock(HyperResource.class);
        when(mockHyperResource.resolveLinkLocal(fakeRelationship)).thenReturn(fakeEmbeddedResource);
        assertSame(fakeEmbeddedResource, proxyTest.resolveLinkLocal(fakeRelationship));
    }

    private HyperLink createHyperLink() {

        return createHyperLink(null);
    }

    private HyperLink createHyperLink(String rel) {

        return new HyperLink(
                UUID.randomUUID().toString(), //Href
                rel, //rel
                false, //templated
                "some type",
                UUID.randomUUID().toString(), //deprecation
                UUID.randomUUID().toString(), //value
                "profile", //prof
                UUID.randomUUID().toString(), //title
                UUID.randomUUID().toString() //href lang
        );
    }


    @Test
    public void testInvokeDataString() throws Exception {

        when(mockHyperResource.getPathAs(String.class, "dataString")).thenReturn("some string");
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals("some string", proxyTest.dataAsString());
    }

    @Test
    public void testInvokeDataIntegerWrapper() throws Exception {

        when(mockHyperResource.getPathAs(Integer.class, "dataIntegerWrapper")).thenReturn(new Integer(123));
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals(new Integer(123), proxyTest.dataAsIntegerWrapper());
    }

    @Test
    public void testInvokeDataIntegerPrimitive() throws Exception {
        when(mockHyperResource.getPathAs(int.class, "dataIntegerPrimitive")).thenReturn(123);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals(proxyTest.dataAsIntegerPrimitive(), 123, 0);
    }

    @Test
    public void testInvokeDataFloatWrapper() throws Exception {
        when(mockHyperResource.getPathAs(Float.class, "dataFloatWrapper")).thenReturn(new Float(123));
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals(new Float(123), proxyTest.dataAsFloatWrapper());
    }

    @Test
    public void testInvokeDataFloatPrimitive() throws Exception {
        when(mockHyperResource.getPathAs(float.class, "dataFloatPrimitive")).thenReturn(123f);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals(123f, proxyTest.dataAsFloatPrimitive(), 0);
    }

    @Test
    public void testInvokeDataLongWrapper() throws Exception {

        when(mockHyperResource.getPathAs(Long.class, "dataLongWrapper")).thenReturn(new Long(123));
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals(new Long(123), proxyTest.dataAsLongWrapper());
    }

    @Test
    public void testInvokeDataLongPrimitive() throws Exception {
        when(mockHyperResource.getPathAs(long.class, "dataLongPrimitive")).thenReturn(123L);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals(123L, proxyTest.dataAsLongPrimitive(), 0);
    }

    @Test
    public void testInvokeDataDoubleWrapper() throws Exception {

        when(mockHyperResource.getPathAs(Double.class, "dataDoubleWrapper")).thenReturn(new Double(123));
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals(new Double(123), proxyTest.dataAsDoubleWrapper());
    }

    @Test
    public void testInvokeDataDoublePrimitive() throws Exception {

        when(mockHyperResource.getPathAs(double.class, "dataDoublePrimitive")).thenReturn(123d);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals(123d, proxyTest.dataAsDoublePrimitive(), 0);
    }

    @Test
    public void testInvokeDataShortWrapper() throws Exception {

        when(mockHyperResource.getPathAs(Short.class, "dataShortWrapper")).thenReturn(new Short((short) 123));
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals(new Short((short) 123), proxyTest.dataAsShortWrapper());
    }

    @Test
    public void testInvokeDataShortPrimitive() throws Exception {

        when(mockHyperResource.getPathAs(short.class, "dataShortPrimitive")).thenReturn((short) 123);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals((short) 123, proxyTest.dataAsShortPrimitive(), 0);
    }

    @Test
    public void testInvokeDataCharacterWrapper() throws Exception {
        when(mockHyperResource.getPathAs(Character.class, "dataCharacterWrapper")).thenReturn(new Character('e'));
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals(new Character('e'), proxyTest.dataAsCharacterWrapper());
    }

    @Test
    public void testInvokeDataCharacterPrimitive() throws Exception {

        when(mockHyperResource.getPathAs(char.class, "dataCharacterPrimitive")).thenReturn('e');
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals('e', proxyTest.dataAsCharacterPrimitive(), 0);
    }

    @Test
    public void testInvokeDataBooleanWrapper() throws Exception {

        when(mockHyperResource.getPathAs(Boolean.class, "dataBooleanWrapper")).thenReturn(Boolean.TRUE);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals(Boolean.TRUE, proxyTest.dataAsBooleanWrapper());
    }

    @Test
    public void testInvokeDataBooleanPrimitive() throws Exception {

        when(mockHyperResource.getPathAs(boolean.class, "dataBooleanPrimitive")).thenReturn(true);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals(true, proxyTest.dataAsBooleanPrimitive());
    }

    @Test
    public void testInvokeDataDateWrapper() throws Exception {

        String dateString = "2014-05-30T16:43:50-0600";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date = simpleDateFormat.parse(dateString);

        when(mockHyperResource.getPathAs(Date.class, "dataDateWrapper")).thenReturn(date);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        assertEquals(date, proxyTest.dataAsDateWrapper());
    }


    /*
    @Test(expected = HyperClientException.class)
    public void testProxyWrappingException() {
        forceProxyException();
    }

    @Test
    public void testProxyWrappingExceptionValues() {
        Exception exception = null;
        try {
            forceProxyException();
        } catch (Exception ex) {
            exception = ex;
        }
        assertNotNull(exception != null && exception.getCause() != null);
        assertTrue(exception instanceof HyperClientException);
    }

    private void forceProxyException() {
        when(mockHyperResource.getValue(new String[]{"dataDateWrapper", "missingNode"})).thenThrow(new RuntimeException(""));
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);
        proxyTest.missingNodeError();
    }
*/

    @Test(expected = HyperResourceException.class)
    public void testInvokeMissingNode() throws Throwable {

        when(mockHyperResource.getPathAs(String.class, "dataDateWrapper", "missingNode"))
            .thenThrow(new HyperResourceException(""));
        DataResource proxyTest = getHyperResourceProxy(DataResource.class, mockHyperResource);

        try {
            proxyTest.missingNodeError();
        } catch (Exception ex) {
            if (null != ex.getCause()) ex = (Exception) ex.getCause();
            throw ex;
        }
    }

    //TODO: need to test retrieval of complex properties

//    @Test
////    public void testInvokeDataResource() throws Exception {
//
//        HyperResource resourceInResource = mock(HyperResource.class);
//        HyperResourceValue resourceResultValue1 = mock(HyperResourceValue.class);
//        HyperResourceValue resourceResultValue2 = mock(HyperResourceValue.class);
//
//        when(resourceResultValue1.getAsString()).thenReturn("string test 1");
//        when(resourceResultValue1.getType()).thenReturn(HyperResourceType.DATA);
//        when(resourceResultValue2.getAsString()).thenReturn("string test 2");
//        when(resourceResultValue2.getType()).thenReturn(HyperResourceType.DATA);
//        when(resourceInResource.getValue("dataAsString1")).thenReturn(resourceResultValue1);
//        when(resourceInResource.getValue("dataAsString2")).thenReturn(resourceResultValue2);
//
//        when(mockHyperResourceValue.getType()).thenReturn(HyperResourceType.RESOURCE);
//        when(mockHyperResourceValue.getAsResource()).thenReturn(resourceInResource);
//        when(mockHyperResource.getValue("dataResource")).thenReturn(mockHyperResourceValue);
//
//        HyperClient spy = PowerMockito.spy(mockHyperClient);
//        Resource proxyTest = getHyperResourceProxy(Resource.class, mockHyperResource, spy);
//        doReturn(
//                getHyperResourceProxy(ResourceInResource.class, resourceInResource, spy)).when(
//                        spy, PowerMockito.method(HyperClient.class, "doProcessResource", 
//                                Class.class, HyperResource.class, Boolean.TYPE) );
//
//        assertEquals(proxyTest.dataAsResource().dataAsString1(), "string test 1");
//        assertEquals(proxyTest.dataAsResource().dataAsString2(), "string test 2");
//    }


    @Test
    public void testInvokeLinkReturningSimpleType() {

        HyperLink expectedLink = createHyperLink();

        when(mockHyperResource.getLink("bb:data", "")).thenReturn(expectedLink);

        DataResource mockDataResource = mock(DataResource.class);

        Request.RequestBuilder expectedHyperRequest = Request.builder()
                .setMethod(Method.GET)
                .setUrlTemplate(expectedLink.getHref());

        when(requestProcessor.processRequest(eq(DataResource.class), eq(expectedHyperRequest), any(TypeInfo.class))).thenReturn(mockDataResource);

        LinkResource p = this.getHyperResourceProxy(LinkResource.class, this.mockHyperResource);

        DataResource result = p.dataResource();

        assertSame(mockDataResource, result);
    }

    @Test
    public void testInvokeLinkReturningString() {

        String rel = "bb:linkstring";
        HyperLink expectedLink = createHyperLink(rel);
        when(mockHyperResource.getLink(rel, "")).thenReturn(expectedLink);

        String expectedStr = UUID.randomUUID().toString();

        Request.RequestBuilder expectedHyperRequest = Request.builder()
                .setMethod(Method.GET)
                .setUrlTemplate(expectedLink.getHref());

        when(requestProcessor.processRequest(eq(String.class), eq(expectedHyperRequest), any(TypeInfo.class))).thenReturn(expectedStr);
        LinkResource linkResource = this.getHyperResourceProxy(LinkResource.class, this.mockHyperResource);

        String result = linkResource.linkString();

        assertSame(expectedStr, result);
    }


    @Test
    public void testInvokeLinkReturningHyperResponse() {

        String rel = "bb:linkHyperResponse";
        HyperLink expectedLink = createHyperLink(rel);
        when(mockHyperResource.getLink(rel, "")).thenReturn(expectedLink);

        Response expectedResponse = new Response.ResponseBuilder().
                addHeader("header", "header").
                addBody("body").
                addCode(200).build();

        Request.RequestBuilder expectedHyperRequest = Request.builder()
                .setMethod(Method.GET)
                .setUrlTemplate(expectedLink.getHref());

        when(requestProcessor.processRequest(eq(Response.class), eq(expectedHyperRequest), any(TypeInfo.class))).thenReturn(expectedResponse);
        LinkResource linkResource = this.getHyperResourceProxy(LinkResource.class, this.mockHyperResource);

        Response result = linkResource.linkHyperResponse();

        assertSame(expectedResponse, result);
    }

    @Test
    public void testInvokeLinkReturningHyperLink() {

        String rel = "bb:hyperLink";
        HyperLink expectedLink = createHyperLink(rel);
        when(mockHyperResource.getLink(rel, "")).thenReturn(expectedLink);

        Request.RequestBuilder expectedHyperRequest = Request.builder()
                .setMethod(Method.GET)
                .setUrlTemplate(expectedLink.getHref());

        when(requestProcessor.processRequest(eq(HyperLink.class), eq(expectedHyperRequest), any(TypeInfo.class))).thenReturn(expectedLink);
        LinkResource linkResource = this.getHyperResourceProxy(LinkResource.class, this.mockHyperResource);

        HyperLink result = linkResource.hyperLink();

        //equals but not the same
        assertEquals(expectedLink, result);

        assertEquals(expectedLink, linkResource.getLink(rel, ""));
    }

    @Test
    public void testInvokeLinkReturningHyperLinks() {

        String rel = "bb:hyperLinks";
        HyperLink[] expectedLinks = {createHyperLink(), createHyperLink(), createHyperLink()};
        when(mockHyperResource.getLinks(rel, "")).thenReturn(expectedLinks);

        Request.RequestBuilder expectedHyperRequest = Request.builder()
                .setMethod(Method.GET)
                .setUrlTemplate("whatever");

        when(requestProcessor.processRequest(eq(HyperLink[].class), eq(expectedHyperRequest), any(TypeInfo.class))).thenReturn(expectedLinks);
        LinkResource linkResource = this.getHyperResourceProxy(LinkResource.class, this.mockHyperResource);

        HyperLink[] result = linkResource.hyperLinks();

        //equals but not the same
        assertArrayEquals(expectedLinks, result);

        assertArrayEquals(expectedLinks, linkResource.getLinks(rel, ""));

    }

    /**
     *{
     *  "_links": {
     *      "some-rel":{"href":"someHref"}
     *
     *  },
     *  "_embedded": {
     *      "some-rel": [ {
     *          "_links": {
     *               "profile":[
     *                      {"href": "/a/b/c/multiple-profile-resource-x"},
     *                      {"href": "/a/b/c/profile-resource-x"},
     *                      {"href": "/a/b/c/profile-resource-1}"
     *                ]
     *          },
     *
     *          "_links":  {
     *               "profile":[
     *                      {"href": "/a/b/c/multiple-profile-resource-x"},
     *                      {"href": "/a/b/c/profile-resource-x"},
     *                      {"href": "/a/b/c/profile-resource-2}"
     *                ]
     *              },
     *          }
     *      } ]
     *  }
     *}
     *
     */
    private ObjectNode createRootNodeForProfileResources(String relationship, String... lastProfiles) {

        //Build up the Hyper resource with embedded item
        ObjectNode root = nodeFactory.objectNode();
        ObjectNode links = nodeFactory.objectNode();
        ObjectNode embedded = nodeFactory.objectNode();
        root.put("_links", links);
        root.put("_embedded", embedded);

        links.put(relationship, nodeFactory.objectNode().put("href", "someHref"));

        ArrayNode embeddedDataNodeArray = nodeFactory.arrayNode();

        for (String lastProfile : lastProfiles) {

            ObjectNode embeddedDataNode = nodeFactory.objectNode();

            ObjectNode embeddedDataNodeLinks = nodeFactory.objectNode();

            embeddedDataNode.put("_links", embeddedDataNodeLinks);

            embeddedDataNodeArray.add(embeddedDataNode);

            ArrayNode profileLinks = nodeFactory.arrayNode();
            profileLinks.add(nodeFactory.objectNode().put("href", "/a/b/c/multiple-profile-resource-x"));
            profileLinks.add(nodeFactory.objectNode().put("href", "/a/b/c/profile-resource-x"));
            profileLinks.add(nodeFactory.objectNode().put("href", "/a/b/c/" + lastProfile));//last

            embeddedDataNodeLinks.put("profile", profileLinks);

        }

        //when there is a single last profile, add just the single node instead of the array
        JsonNode aNode = (lastProfiles.length == 1) ? embeddedDataNodeArray.get(0) : embeddedDataNodeArray;
        embedded.put(relationship, aNode);

        return root;
    }


    @Test
    public void testInvokeSingleProfileResourceTest() {

        String relationship = "bb:profile-resource-1";
        ObjectNode root = createRootNodeForProfileResources(relationship, "profile-resource-1");

        HalJsonResource fakeResource = new HalJsonResource(root);
        HyperRequestProcessor hyperRequestProcessor = new HyperRequestProcessor(new RootResourceBuilder().resourceRegistry(this.resourceRegistry));
        LinkResource linkResource = this.getHyperResourceProxy(LinkResource.class, fakeResource, hyperRequestProcessor);

        //resolve the relationship via method
        BaseProfileResource profileResource1 = linkResource.profileResource1();

        assertTrue(profileResource1 instanceof ProfileResource1);

        //Now resolve the relationship via HyperLink
        HyperLink hyperLink = linkResource.getLink(relationship);
        ProfileResource1 actual = hyperLink.follow(new TypeRef<ProfileResource1>() {});

        assertEquals(actual, profileResource1);
    }

    @Test
    public void testInvokeMultipleProfileResourceTest() throws Exception {

        HyperRequestProcessor hyperRequestProcessor = new HyperRequestProcessor(new RootResourceBuilder().resourceRegistry(this.resourceRegistry));

        String relationship = "bb:multiple-profile-resource";

        ObjectNode root = createRootNodeForProfileResources(relationship, "multiple-profile-resource-a");
        HalJsonResource fakeResource = new HalJsonResource(root);
        LinkResource linkResource = this.getHyperResourceProxy(LinkResource.class, fakeResource, hyperRequestProcessor);

        //resolve the relationship via method
        BaseProfileResource multipleProfileResource = linkResource.multipleProfileResource();

        assertTrue(multipleProfileResource instanceof MultipleProfileResource);

        //Now resolve the relationship via HyperLink
        HyperLink hyperLink = linkResource.getLink(relationship);
        MultipleProfileResource fromFollow = hyperLink.follow(new TypeRef<MultipleProfileResource>() {});

        assertEquals(fromFollow, multipleProfileResource);


        root = createRootNodeForProfileResources(relationship, "multiple-profile-resource-b");
        fakeResource = new HalJsonResource(root);
        linkResource = this.getHyperResourceProxy(LinkResource.class, fakeResource, hyperRequestProcessor);
        //resolve the relationship via method
        BaseProfileResource multipleProfileResource2 = linkResource.multipleProfileResource();

        assertTrue(multipleProfileResource2 instanceof MultipleProfileResource);

        //Now resolve the relationship via HyperLink
        hyperLink = linkResource.getLink(relationship);
        fromFollow = hyperLink.follow(new TypeRef<MultipleProfileResource>() {});

        assertEquals(fromFollow, multipleProfileResource2);

        //their last profile is different
        assertNotEquals(multipleProfileResource, multipleProfileResource2);

    }

    @Test
    public void testInvokeArrayProfileResourceTest() {

        String relationship = "bb:profile-resources";
        ObjectNode root = createRootNodeForProfileResources(relationship, "profile-resource-1", "profile-resource-2", "multiple-profile-resource-b");

        HalJsonResource fakeResource = new HalJsonResource(root);
        HyperRequestProcessor hyperRequestProcessor = new HyperRequestProcessor(new RootResourceBuilder().resourceRegistry(this.resourceRegistry));

        LinkResource linkResource = this.getHyperResourceProxy(LinkResource.class, fakeResource, hyperRequestProcessor);

        //resolve relationship via method
        BaseProfileResource[] profileResources = linkResource.profileResources();

        assertTrue(profileResources[0] instanceof ProfileResource1);
        assertTrue(profileResources[1] instanceof ProfileResource2);
        assertTrue(profileResources[2] instanceof MultipleProfileResource);

        /*Now resolve the relationship via HyperLink

        HyperLink[] hyperLink = linkResource.getLinks(relationship);
        ProfileResource1 fromFollow = hyperLink[0].follow(new TypeRef<ProfileResource1>() {});

        assertEquals(fromFollow, profileResources[0]);
        */
    }


    @Test
    public void testInvokeNotInRegistryResourceTest() {

        //Build up the Hyper resource with embedded item
        ObjectNode root = createRootNodeForProfileResources("bb:not-in-registry-resource", "not-in-registry-resource");

        HalJsonResource fakeResource = new HalJsonResource(root);
        HyperRequestProcessor hyperRequestProcessor = new HyperRequestProcessor(new RootResourceBuilder());
        LinkResource linkResource = this.getHyperResourceProxy(LinkResource.class, fakeResource, hyperRequestProcessor);

        BaseProfileResource profileResource = linkResource.notInRegistryResource();

        assertFalse(profileResource instanceof NotInRegistryResource);
    }


    /**
     *{
     *  "_links": {
     *      "bb:data": {"href":"neverGonnaBeUsed", "name":"the_name"}
     *  },
     *  "_embedded": {
     *      "bb:data": {
     *          "_links": {
     *              "self":{},
     *          },
     *          "dataString":"abcdef"
     *      }
     *  }
     *}
     */
    @Test
    public void testInvokeLinkReturningResourceResolvedLocally() {
        //Verifies the ability to pull a single resource out of an embedded collection

        String relationship = "bb:data";
        String NAME = "the_name";

        //Build up the Hyper resource with embedded item
        ObjectNode root = nodeFactory.objectNode();
        ObjectNode links = nodeFactory.objectNode();
        ObjectNode embedded = nodeFactory.objectNode();
        root.put("_links", links);
        root.put("_embedded", embedded);

        ObjectNode embeddedDataNode = nodeFactory.objectNode();
        embedded.put(relationship, embeddedDataNode);

        ObjectNode embeddedDataNodeLinks = nodeFactory.objectNode();
        embeddedDataNode.put("_links", embeddedDataNodeLinks);

        ObjectNode dataSelfLink = nodeFactory.objectNode();
        embeddedDataNodeLinks.put("self", dataSelfLink);

        //Put a link for the embedded relationship...it should never be followed
        ObjectNode dataLink = nodeFactory.objectNode();
        dataLink.put("href", "neverGonnaBeUsed");
        dataLink.put("name", NAME);
        links.put(relationship, dataLink);

        String dataStringValue = UUID.randomUUID().toString();
        embeddedDataNode.put("dataString", dataStringValue);

        HalJsonResource fakeResource = new HalJsonResource(root);

        HyperRequestProcessor hyperRequestProcessor = new HyperRequestProcessor(new RootResourceBuilder());

        LinkResource linkResource = this.getHyperResourceProxy(LinkResource.class, fakeResource, hyperRequestProcessor);


        //Resolve the relationship via methods
        DataResource actual = linkResource.dataResource();

        assertEquals(dataStringValue, actual.dataAsString());

        //Now resolve the relationship via HyperLink
        HyperLink hyperLink = linkResource.getLink(relationship);
        DataResource actual2 = hyperLink.follow(new TypeRef<DataResource>() {});

        assertEquals(dataStringValue, actual2.dataAsString());

        HyperLink hyperLink2 = linkResource.dataLink();

        assertEquals(hyperLink, hyperLink2);
        assertEquals(hyperLink2.getName(), NAME);

    }

    @Test
    public void testInvokeLinkReturningArrayOfResourcesResolvedLocally() {
        //Verifies the ability to pull multiple resources out of a embedded link into an array

        String relationship = "bb:datas";

        //Build up the Hyper resource with embedded item
        ObjectNode root = nodeFactory.objectNode();
        ObjectNode links = nodeFactory.objectNode();
        ObjectNode embedded = nodeFactory.objectNode();
        root.put("_links", links);
        root.put("_embedded", embedded);


        ArrayNode dataNodes = nodeFactory.arrayNode();
        embedded.put(relationship, dataNodes);

        ObjectNode dataNode1 = nodeFactory.objectNode();
        dataNodes.add(dataNode1);
        ObjectNode dataNode1Links = nodeFactory.objectNode();

        dataNode1.put("_links", dataNode1Links);
        ObjectNode dataSelfLink = nodeFactory.objectNode();
        dataNode1Links.put("self", dataSelfLink);

        String data1StringValue = UUID.randomUUID().toString();
        dataNode1.put("dataString", data1StringValue);

        ObjectNode dataNode2 = nodeFactory.objectNode();
        dataNodes.add(dataNode2);
        ObjectNode dataNode2Links = nodeFactory.objectNode();

        dataNode2.put("_links", dataNode2Links);
        ObjectNode data2SelfLink = nodeFactory.objectNode();
        dataNode2Links.put("self", data2SelfLink);

        String data2StringValue = UUID.randomUUID().toString();
        dataNode2.put("dataString", data2StringValue);

        HalJsonResource fakeResource = new HalJsonResource(root);

        HyperRequestProcessor hyperRequestProcessor = new HyperRequestProcessor(new RootResourceBuilder());

        LinkResource p = this.getHyperResourceProxy(LinkResource.class, fakeResource, hyperRequestProcessor);

        //Resolve the link
        DataResource[] actual = p.dataResourcesArray();

        assertEquals(2, actual.length);

        DataResource actual1 = actual[0];
        assertEquals(data1StringValue, actual1.dataAsString());

        DataResource actual2 = actual[1];
        assertEquals(data2StringValue, actual2.dataAsString());

    }

    /* TODO: re-enable this test when we add back strategies

    @Test
    public void testInvokeLinkReturningListOfResourcesResolvedLocally() {
        //Verifies the ability to pull multiple resources out of a embedded link into a List

        String relationship = "bb:datas";

        //Build up the Hyper resource with embedded item
        ObjectNode root = nodeFactory.objectNode();
        ObjectNode links = nodeFactory.objectNode();
        ObjectNode embedded = nodeFactory.objectNode();
        root.put("_links", links);
        root.put("_embedded", embedded);

        ArrayNode dataNodes = nodeFactory.arrayNode();
        embedded.put(relationship, dataNodes);

        ObjectNode dataNode1 = nodeFactory.objectNode();
        dataNodes.add(dataNode1);
        ObjectNode dataNode1Links = nodeFactory.objectNode();

        dataNode1.put("_links", dataNode1Links);
        ObjectNode dataSelfLink = nodeFactory.objectNode();
        dataNode1Links.put("self", dataSelfLink);

        String data1StringValue = UUID.randomUUID().toString();
        dataNode1.put("dataString", data1StringValue);

        ObjectNode dataNode2 = nodeFactory.objectNode();
        dataNodes.add(dataNode2);
        ObjectNode dataNode2Links = nodeFactory.objectNode();

        dataNode2.put("_links", dataNode2Links);
        ObjectNode data2SelfLink = nodeFactory.objectNode();
        dataNode2Links.put("self", data2SelfLink);

        String data2StringValue = UUID.randomUUID().toString();
        dataNode2.put("dataString", data2StringValue);

        HalJsonResource fakeResource = new HalJsonResource(root);

        HyperRequestProcessor hyperRequestProcessor = new HyperRequestProcessor(new RootResourceBuilder());

        LinkResource p = this.getHyperResourceProxy(LinkResource.class, fakeResource, hyperRequestProcessor);

        //Resolve the link
        List<DataResource> actual = p.dataResourcesList();

        assertEquals(2, actual.size());

        DataResource actual1 = actual.get(0);
        assertEquals(data1StringValue, actual1.dataAsString());

        DataResource actual2 = actual.get(1);
        assertEquals(data2StringValue, actual2.dataAsString());


    }
    */

    @Test(expected = UnsupportedOperationException.class)
    public void multipleLinksNotSupported() throws Throwable {

        String relationship = "bb:datas";
        ObjectNode root = nodeFactory.objectNode();
        ObjectNode links = nodeFactory.objectNode();
        root.put("_links", links);

        ArrayNode linkArray = nodeFactory.arrayNode();

        ObjectNode l1 = nodeFactory.objectNode();
        l1.put("href", "/first-item");
        l1.put("title", "first");

        ObjectNode l2 = nodeFactory.objectNode();
        l2.put("href", "/second-item");
        l2.put("title", "second");

        linkArray.add(l1);
        linkArray.add(l2);

        links.put(relationship, linkArray);

        HalJsonResource fakeResource = new HalJsonResource(root);

        HyperRequestProcessor hyperRequestProcessor = new HyperRequestProcessor(new RootResourceBuilder());

        LinkResource p = this.getHyperResourceProxy(LinkResource.class, fakeResource, hyperRequestProcessor);

        try {
            p.multiLinkResources();
        } catch (Exception ex) {
            throw ex.getCause();
        }

        /**
         {"_links":
         {"bb:datas":
         [
         {"href":"/first-item","title":"first"},
         {"href":"/second-item","title":"second"}
         ]
         }
         }
         **/
    }



    @Test(expected = ServiceException.class)
    public void testBuildResourceNoContentTypeException() {
        HyperRequestProcessor hyperRequestProcessor = new HyperRequestProcessor(new RootResourceBuilder());
        Response response = new Response.ResponseBuilder().build();
        hyperRequestProcessor.buildHyperResource(response);
    }

    @Test(expected = ServiceException.class)
    public void testBuildResourceNoHyperMediaTypeHandlerException() {
        HyperRequestProcessor hyperRequestProcessor = new HyperRequestProcessor(new RootResourceBuilder());
        Response response = new Response.ResponseBuilder().
                addHeader(HttpHeader.CONTENT_TYPE, "someType").
                build();
        hyperRequestProcessor.buildHyperResource(response);
    }


    /*


    private void forceParameterizedTypeException() {
        HyperRequestProcessor responseProcessor = mock(HyperRequestProcessor.class);

        HyperResource hyperResource = mock(HyperResource.class);
        HyperResourceInvokeHandler hyperResourceInvokeHandler =
                new HyperResourceInvokeHandler(hyperResource, responseProcessor, resourceMethodInfoCache.get(hyperResource.getClass()), new TypeInfo());

        HyperResourcePart hyperResourcePart = mock(HyperResourcePart.class);

        when(hyperResourcePart.getType()).thenReturn(HyperResourceType.ARRAY);
        GenericArrayType genericReturnType = mock(GenericArrayType.class);

        TypeVariable typeVariable = mock(TypeVariable.class);
        when(genericReturnType.getGenericComponentType()).thenReturn(typeVariable);
        when(typeVariable.getName()).thenReturn("you-wont-find-me");

        hyperResourceInvokeHandler.processReturnValue(Object[].class, genericReturnType, hyperResourcePart);
    }

    @Test(expected = ParameterizedTypeException.class)
    public void testParameterizedTypeExceptionThrownWhenTypeNotFound() {
        forceParameterizedTypeException();
    }

    @Test
    public void testParameterizedTypeException() {
        ParameterizedTypeException parameterizedTypeException = null;
        try {
            forceParameterizedTypeException();
        } catch (ParameterizedTypeException ex) {
            parameterizedTypeException = ex;
        }
        assertNotNull(parameterizedTypeException);
        assertEquals(MessageFormatter.arrayFormat(Messages.MSG_ERROR_LOOKING_FOR_PARAM, new Object[]{"you-wont-find-me"}).getMessage(),
            parameterizedTypeException.getMessage());

    }
    */

    private static List<Class<? extends HyperResource>> scanClasses(String thePackage) {
        List<Class<? extends HyperResource>> classList = new ArrayList<Class<? extends HyperResource>>();

        try {
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            ClassPath classpath = ClassPath.from(loader);
            for (ClassPath.ClassInfo info : classpath.getTopLevelClassesRecursive(thePackage)) {
                final Class<?> clazz = info.load();
                if (HyperResource.class.isAssignableFrom(clazz)) {
                    classList.add((Class<? extends HyperResource>)clazz);
                }
            }
        }  catch (Exception e) {
            //DO NOTHING
        }

        return classList;
    }


}
