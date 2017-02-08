package org.hyperfit;


import org.hyperfit.annotation.*;
import org.hyperfit.net.*;
import org.hyperfit.methodinfo.ConcurrentHashMapResourceMethodInfoCache;
import org.hyperfit.methodinfo.ResourceMethodInfoCache;
import org.hyperfit.net.Method;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.HyperResourceException;
import org.hyperfit.resource.controls.form.CheckboxField;
import org.hyperfit.resource.controls.form.Form;
import org.hyperfit.resource.controls.link.HyperLink;
import org.hyperfit.utils.TypeInfo;
import org.hyperfit.utils.TypeRef;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static test.TestUtils.*;

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
        String dataAsString();

        @Data({"dataIntegerWrapper"})
        Integer dataAsIntegerWrapper();

        @Data({"dataIntegerPrimitive"})
        int dataAsIntegerPrimitive();

        @Data({"dataFloatWrapper"})
        Float dataAsFloatWrapper();

        @Data({"dataFloatPrimitive"})
        float dataAsFloatPrimitive();

        @Data({"dataLongWrapper"})
        Long dataAsLongWrapper();

        @Data({"dataLongPrimitive"})
        long dataAsLongPrimitive();

        @Data({"dataDoubleWrapper"})
        Double dataAsDoubleWrapper();

        @Data({"dataDoublePrimitive"})
        double dataAsDoublePrimitive();

        @Data({"dataShortWrapper"})
        Short dataAsShortWrapper();

        @Data({"dataShortPrimitive"})
        short dataAsShortPrimitive();

        @Data({"dataCharacterWrapper"})
        Character dataAsCharacterWrapper();

        @Data({"dataCharacterPrimitive"})
        char dataAsCharacterPrimitive();

        @Data({"dataBooleanWrapper"})
        Boolean dataAsBooleanWrapper();

        @Data({"dataBooleanPrimitive"})
        boolean dataAsBooleanPrimitive();

        @Data({"dataDateWrapper"})
        Date dataAsDateWrapper();

        @Data({"dataDateWrapper", "missingNode"})
        String missingNodeError();

        @Data("complexData")
        ComplexProperty complexData();

        @Data(value = "anotherNullableNode")
        @NullWhenMissing
        Object iCanBeNull();

        @Link(value = "nullableLink")
        @NullWhenMissing
        Object followNullWhenMissingLink();

        @NamedLink(rel= "namedNullableLink", name = "goingToBeNull")
        @NullWhenMissing
        Object followNullWhenMissingNamedLink();

        @Link(value = "nullableLink")
        @NullWhenMissing
        HyperLink getNullWhenMissingLink();

        @NamedLink(rel= "namedNullableLink", name = "goingToBeNull")
        @NullWhenMissing
        HyperLink getNullWhenMissingNamedLink();


        @Link(value = "nullableLink")
        @NullWhenMissing
        HyperLink[] getNullWhenMissingLinkArray();

        @NamedLink(rel= "namedNullableLink", name = "goingToBeNull")
        @NullWhenMissing
        HyperLink[] getNullWhenMissingNamedLinkArray();
    }


    public interface LinkResource extends HyperResource {
        @Link("bb:data")
        DataResource dataResource();

        @Link("bb:data")
        HyperLink dataLink();

        @Link("bb:datas")
        DataResource[] dataResourcesArray();

        @Link("bb:linkstring")
        String linkString();

        @Link("bb:linkHyperResponse")
        Response linkHyperResponse();

        @Link("bb:hyperLink")
        HyperLink hyperLink();

        @Link(value = "bb:hyperLinks")
        HyperLink[] hyperLinks();
    }

    public interface FirstLinkResource extends HyperResource {

        @FirstLink(rel="x:first-link", names={})
        HyperLink firstLinkEmptyNames();

        @FirstLink(rel="x:first-link", names={FirstLink.MATCH_ANY_NAME})
        HyperLink firstLinkWildCard();

        @FirstLink(rel="x:first-link", names={FirstLink.NULL})
        HyperLink firstLinkNullName();

        @FirstLink(rel="x:first-link", names={"test"})
        HyperLink firstLinkTestName();

        @FirstLink(rel="x:first-link", names={"test", FirstLink.MATCH_ANY_NAME})
        HyperLink firstLinkTestNameThenWildCard();

        @FirstLink(rel="x:first-link", names={"test1", "test2"})
        HyperLink firstLinkTest1NameThenTest2Name();

        @FirstLink(rel="x:first-link", names={"test", ""})
        HyperLink firstLinkTestNameThenEmptyName();

        @FirstLink(rel="x:first-link", names={"test", FirstLink.NULL})
        HyperLink firstLinkTestNameThenNullName();
    }

    public interface FormResource extends HyperResource {
        @NamedForm("FormA")
        boolean hasFormA();

        @NamedForm("FormA")
        Form getFormA();

        @NamedForm("FormA")
        FormResource formAResource();


        @NamedForm("FormA")
        FormResource formAResource(
            @Param("Field1") String param1,
            @Param("Field2") CheckboxField.CheckState checked
        );



        @NamedForm("FormB")
        boolean hasFormB();

        @NamedForm("FormB")
        Form getFormB();


    }


    @Mock
    protected HyperResource mockHyperResource;

    @Mock
    private HyperfitProcessor mockHyperfitProcessor;

    private ResourceMethodInfoCache resourceMethodInfoCache;



    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        resourceMethodInfoCache = new ConcurrentHashMapResourceMethodInfoCache();
    }


    <T> T getHyperResourceProxy(Class<T> clazz) {
        return clazz.cast(
            Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new HyperResourceInvokeHandler(mockHyperResource, mockHyperfitProcessor, resourceMethodInfoCache.get(clazz), null)
            )
        );
    }



    @Test
    public void testInvokeToString() throws Exception {
        when(mockHyperResource.toString()).thenReturn("test");
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals("test", proxyTest.toString());
    }

    @Test
    public void testInvokeHashCode() throws Exception {

        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals(mockHyperResource.hashCode(), proxyTest.hashCode());
    }

    @Test
    public void testInvokeEquals() throws Exception {

        DataResource proxyTest1 = getHyperResourceProxy(DataResource.class);
        DataResource proxyTest2 = getHyperResourceProxy(DataResource.class);
        assertTrue(proxyTest1.equals(proxyTest2));
    }

    @Test
    public void testInvokeHyperResourceMethods() throws Exception {

        DataResource proxyTest = getHyperResourceProxy(DataResource.class);


        String fakeRelationship = uniqueString();
        HyperLink expectedLink = makeLink(fakeRelationship);
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


    @Test
    public void testInvokeDataString() throws Exception {

        when(mockHyperResource.getPathAs(String.class, false,"dataString")).thenReturn("some string");
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals("some string", proxyTest.dataAsString());
    }

    @Test
    public void testInvokeDataIntegerWrapper() throws Exception {

        when(mockHyperResource.getPathAs(Integer.class, false,"dataIntegerWrapper")).thenReturn(new Integer(123));
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals(new Integer(123), proxyTest.dataAsIntegerWrapper());
    }

    @Test
    public void testInvokeDataIntegerPrimitive() throws Exception {
        when(mockHyperResource.getPathAs(int.class, false,"dataIntegerPrimitive")).thenReturn(123);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals(123, proxyTest.dataAsIntegerPrimitive(), 0);
    }

    @Test
    public void testInvokeDataFloatWrapper() throws Exception {
        when(mockHyperResource.getPathAs(Float.class, false,"dataFloatWrapper")).thenReturn(new Float(123));
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals(new Float(123), proxyTest.dataAsFloatWrapper());
    }

    @Test
    public void testInvokeDataFloatPrimitive() throws Exception {
        when(mockHyperResource.getPathAs(float.class, false,"dataFloatPrimitive")).thenReturn(123f);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals(123f, proxyTest.dataAsFloatPrimitive(), 0);
    }

    @Test
    public void testInvokeDataLongWrapper() throws Exception {

        when(mockHyperResource.getPathAs(Long.class, false,"dataLongWrapper")).thenReturn(new Long(123));
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals(new Long(123), proxyTest.dataAsLongWrapper());
    }

    @Test
    public void testInvokeDataLongPrimitive() throws Exception {
        when(mockHyperResource.getPathAs(long.class, false,"dataLongPrimitive")).thenReturn(123L);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals(123L, proxyTest.dataAsLongPrimitive(), 0);
    }

    @Test
    public void testInvokeDataDoubleWrapper() throws Exception {

        when(mockHyperResource.getPathAs(Double.class, false,"dataDoubleWrapper")).thenReturn(new Double(123));
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals(new Double(123), proxyTest.dataAsDoubleWrapper());
    }

    @Test
    public void testInvokeDataDoublePrimitive() throws Exception {

        when(mockHyperResource.getPathAs(double.class, false,"dataDoublePrimitive")).thenReturn(123d);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals(123d, proxyTest.dataAsDoublePrimitive(), 0);
    }

    @Test
    public void testInvokeDataShortWrapper() throws Exception {

        when(mockHyperResource.getPathAs(Short.class, false,"dataShortWrapper")).thenReturn(new Short((short) 123));
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals(new Short((short) 123), proxyTest.dataAsShortWrapper());
    }

    @Test
    public void testInvokeDataShortPrimitive() throws Exception {

        when(mockHyperResource.getPathAs(short.class, false,"dataShortPrimitive")).thenReturn((short) 123);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals((short) 123, proxyTest.dataAsShortPrimitive(), 0);
    }

    @Test
    public void testInvokeDataCharacterWrapper() throws Exception {
        when(mockHyperResource.getPathAs(Character.class, false,"dataCharacterWrapper")).thenReturn(new Character('e'));
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals(new Character('e'), proxyTest.dataAsCharacterWrapper());
    }

    @Test
    public void testInvokeDataCharacterPrimitive() throws Exception {

        when(mockHyperResource.getPathAs(char.class, false,"dataCharacterPrimitive")).thenReturn('e');
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals('e', proxyTest.dataAsCharacterPrimitive(), 0);
    }

    @Test
    public void testInvokeDataBooleanWrapper() throws Exception {

        when(mockHyperResource.getPathAs(Boolean.class, false,"dataBooleanWrapper")).thenReturn(Boolean.TRUE);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals(Boolean.TRUE, proxyTest.dataAsBooleanWrapper());
    }

    @Test
    public void testInvokeDataBooleanPrimitive() throws Exception {

        when(mockHyperResource.getPathAs(boolean.class, false,"dataBooleanPrimitive")).thenReturn(true);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
        assertEquals(true, proxyTest.dataAsBooleanPrimitive());
    }

    @Test
    public void testInvokeDataDateWrapper() throws Exception {

        String dateString = "2014-05-30T16:43:50-0600";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date = simpleDateFormat.parse(dateString);

        when(mockHyperResource.getPathAs(Date.class, false,"dataDateWrapper")).thenReturn(date);
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);
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

        when(mockHyperResource.getPathAs(String.class, false,"dataDateWrapper", "missingNode"))
            .thenThrow(new HyperResourceException(""));
        DataResource proxyTest = getHyperResourceProxy(DataResource.class);

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

        String rel = "bb:data";

        HyperLink expectedLink = makeLink(rel);
        when(mockHyperResource.getLink(rel)).thenReturn(expectedLink);

        DataResource mockDataResource = mock(DataResource.class);

        RequestBuilder expectedHyperRequest = new BoringRequestBuilder()
            .setUrl(expectedLink.getHref())
            .setMethod(Method.GET);

        when(mockHyperfitProcessor.processRequest(eq(DataResource.class), eq(expectedHyperRequest), any(TypeInfo.class)))
            .thenReturn(mockDataResource);

        LinkResource p = this.getHyperResourceProxy(LinkResource.class);

        DataResource result = p.dataResource();

        assertSame(mockDataResource, result);
    }

    @Test
    public void testInvokeLinkReturningString() {

        String rel = "bb:linkstring";
        HyperLink expectedLink = makeLink(rel);
        when(mockHyperResource.getLink(rel)).thenReturn(expectedLink);

        String expectedStr = uniqueString();

        RequestBuilder expectedHyperRequest = new BoringRequestBuilder()
            .setUrl(expectedLink.getHref())
            .setMethod(Method.GET)
            ;


        when(mockHyperfitProcessor.processRequest(eq(String.class), eq(expectedHyperRequest), any(TypeInfo.class)))
            .thenReturn(expectedStr);

        LinkResource linkResource = this.getHyperResourceProxy(LinkResource.class);

        String result = linkResource.linkString();

        assertSame(expectedStr, result);
    }


    @Test
    public void testInvokeLinkReturningHyperResponse() {

        String rel = "bb:linkHyperResponse";
        HyperLink expectedLink = makeLink(rel);
        when(mockHyperResource.getLink(rel)).thenReturn(expectedLink);

        RequestBuilder expectedHyperRequest = new BoringRequestBuilder()
            .setUrl(expectedLink.getHref())
            .setMethod(Method.GET)
            ;

        Response expectedResponse = new Response.ResponseBuilder()
                .addHeader("header", "header")
                .addRequest(expectedHyperRequest.build())
                .addBody("body")
                .addCode(200)
                .build();


        when(mockHyperfitProcessor.processRequest(eq(Response.class), eq(expectedHyperRequest), any(TypeInfo.class)))
            .thenReturn(expectedResponse);

        LinkResource linkResource = this.getHyperResourceProxy(LinkResource.class);

        Response result = linkResource.linkHyperResponse();

        assertSame(expectedResponse, result);
    }

    @Test
    public void testInvokeLinkReturningHyperLink() {

        String rel = "bb:hyperLink";
        HyperLink expectedLink = makeLink(rel);
        when(mockHyperResource.getLink(rel)).thenReturn(expectedLink);

        RequestBuilder expectedHyperRequest = new RFC6570RequestBuilder()
                .setMethod(Method.GET)
                .setUrlTemplate(expectedLink.getHref());

        when(mockHyperfitProcessor.processRequest(eq(HyperLink.class), eq(expectedHyperRequest), any(TypeInfo.class)))
            .thenReturn(expectedLink);

        LinkResource linkResource = this.getHyperResourceProxy(LinkResource.class);

        HyperLink result = linkResource.hyperLink();

        //equals but not the same
        assertEquals(expectedLink, result);

        assertEquals(expectedLink, linkResource.getLink(rel));
    }

    @Test
    public void testInvokeLinkReturningHyperLinks() {

        String rel = "bb:hyperLinks";
        HyperLink[] expectedLinks = {makeLink(rel), makeLink(rel), makeLink(rel)};
        when(mockHyperResource.getLinks(rel)).thenReturn(expectedLinks);

        RequestBuilder expectedHyperRequest = new RFC6570RequestBuilder()
                .setMethod(Method.GET)
                .setUrlTemplate("whatever");

        when(mockHyperfitProcessor.processRequest(eq(HyperLink[].class), eq(expectedHyperRequest), any(TypeInfo.class)))
            .thenReturn(expectedLinks);

        LinkResource linkResource = this.getHyperResourceProxy(LinkResource.class);

        HyperLink[] result = linkResource.hyperLinks();

        //equals but not the same
        assertArrayEquals(expectedLinks, result);

        assertArrayEquals(expectedLinks, linkResource.getLinks(rel));

    }








    @Test
    public void testInvokeLinkReturningResourceResolvedLocally() {
        //Verifies the ability to pull a single resource out of an embedded collection

        String relationship = "bb:data";

        LinkResource linkResource = this.getHyperResourceProxy(LinkResource.class);

        DataResource expected = mock(DataResource.class);

        //Resolve the relationship via method
        when(mockHyperResource.canResolveLinkLocal(relationship))
            .thenReturn(true);
        when(mockHyperResource.resolveLinkLocal(relationship))
            .thenReturn(expected);

        when(mockHyperfitProcessor.processResource(eq(DataResource.class), eq(expected), any(TypeInfo.class)))
            .thenReturn(expected);

        assertEquals(expected, linkResource.dataResource());

        //Now resolve the relationship via HyperLink retrieved directly

        HyperLink fakeLink = makeLink(relationship);
        when(mockHyperResource.getLink(relationship)).thenReturn(fakeLink);
        HyperLink hyperLink = linkResource.getLink(relationship);

        when(mockHyperfitProcessor.processRequest(eq(DataResource.class), any(RequestBuilder.class), any(TypeInfo.class)))
            .thenReturn(expected);
        DataResource actual2 = hyperLink.follow(new TypeRef<DataResource>() {});

        assertEquals(expected, actual2);


        //Now resolve the relationship via HyperLink retrieved via method
        when(mockHyperResource.getLink(relationship)).thenReturn(fakeLink);
        hyperLink = linkResource.dataLink();
        actual2 = hyperLink.follow(new TypeRef<DataResource>() {});

        assertEquals(expected, actual2);


    }

    @Test
    public void testInvokeLinkReturningArrayOfResourcesResolvedLocally() {
        //Verifies the ability to pull multiple resources out of a embedded link into an array

        String relationship = "bb:datas";

        when(mockHyperResource.canResolveLinkLocal(relationship))
            .thenReturn(true);

        DataResource[] expected = new DataResource[2];
        when(mockHyperResource.resolveLinksLocal(relationship))
            .thenReturn(expected);

        LinkResource p = this.getHyperResourceProxy(LinkResource.class);

        //Resolve the link with annotated method
        DataResource[] actual = p.dataResourcesArray();

        assertEquals(2, actual.length);
        assertArrayEquals(expected, actual);


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

        String data1StringValue = uniqueString();
        dataNode1.put("dataString", data1StringValue);

        ObjectNode dataNode2 = nodeFactory.objectNode();
        dataNodes.add(dataNode2);
        ObjectNode dataNode2Links = nodeFactory.objectNode();

        dataNode2.put("_links", dataNode2Links);
        ObjectNode data2SelfLink = nodeFactory.objectNode();
        dataNode2Links.put("self", data2SelfLink);

        String data2StringValue = uniqueString();
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

        LinkResource p = this.getHyperResourceProxy(LinkResource.class);
        when(mockHyperResource.isMultiLink(relationship)).thenReturn(true);

        try {
            p.dataResourcesArray();
        } catch (Exception ex) {
            throw ex.getCause();
        }

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



// BEGIN FirstLink annotated method


    @Test(expected = HyperResourceException.class)
    public void testGetFirstLinkNoLinksAtAllEmptyNames() {
        String relationship = "x:first-link";

        FirstLinkResource r = this.getHyperResourceProxy(FirstLinkResource.class);

        when(mockHyperResource.getLinks(relationship))
            .thenReturn(new HyperLink[0]);

        HyperLink results = r.firstLinkEmptyNames();

    }

    @Test(expected = HyperResourceException.class)
    public void testGetFirstLinkNoLinksAtAllWildCardNames() {
        String relationship = "x:first-link";

        FirstLinkResource r = this.getHyperResourceProxy(FirstLinkResource.class);

        when(mockHyperResource.getLinks(relationship))
            .thenReturn(new HyperLink[0]);

        HyperLink results = r.firstLinkWildCard();

    }

    @Test(expected = HyperResourceException.class)
    public void testGetFirstLinkNoLinksAtAllNullNames() {
        String relationship = "x:first-link";

        FirstLinkResource r = this.getHyperResourceProxy(FirstLinkResource.class);

        when(mockHyperResource.getLinks(relationship))
            .thenReturn(new HyperLink[0]);

        HyperLink results = r.firstLinkNullName();

    }



    @Test(expected = HyperResourceException.class)
    public void testGetFirstLinkMultipleLinksNoneMatchEmptyNames() {
        String relationship = "x:first-link";

        FirstLinkResource r = this.getHyperResourceProxy(FirstLinkResource.class);

        when(mockHyperResource.getLinks(relationship)).thenReturn(
            new HyperLink[]{
                makeLink(relationship, "name1"),
                makeLink(relationship, "name2"),
                makeLink(relationship, "name3")
            }
        );

        r.firstLinkEmptyNames();
    }

    @Test(expected = HyperResourceException.class)
    public void testGetFirstLinkMultipleLinksNoneMatchNullNames() {
        String relationship = "x:first-link";

        FirstLinkResource r = this.getHyperResourceProxy(FirstLinkResource.class);

        when(mockHyperResource.getLinks(relationship))
            .thenReturn(
                new HyperLink[]{
                    makeLink(relationship, "name1"),
                    makeLink(relationship, "name2"),
                    makeLink(relationship, "name3")
                }
            );

        r.firstLinkNullName();
    }

    @Test(expected = HyperResourceException.class)
    public void testGetFirstLinkMultipleLinksNoneMatchTestName() {
        String relationship = "x:first-link";

        FirstLinkResource r = this.getHyperResourceProxy(FirstLinkResource.class);

        when(mockHyperResource.getLinks(relationship))
            .thenReturn(
                new HyperLink[]{
                    makeLink(relationship, "name1"),
                    makeLink(relationship, "name2"),
                    makeLink(relationship, "name3")
                }
            );

        r.firstLinkTestName();
    }


    @Test
    public void testGetFirstLinkUsingWildCard() {
        String relationship = "x:first-link";

        FirstLinkResource r = this.getHyperResourceProxy(FirstLinkResource.class);

        HyperLink link1 = makeLink(relationship, "name1");
        when(mockHyperResource.getLinks(relationship)).thenReturn(
            new HyperLink[]{
                link1,
                makeLink(relationship, "name2"),
                makeLink(relationship, "name3")
            }
        );
        when(mockHyperResource.getLink(relationship, "name1")).thenReturn(link1);

        HyperLink result = r.firstLinkWildCard();
        assertEquals(link1, result);
    }


    @Test
    public void testGetFirstLinkUsingNoMatchThenWildCard() {
        String relationship = "x:first-link";

        FirstLinkResource r = this.getHyperResourceProxy(FirstLinkResource.class);

        HyperLink link1 = makeLink(relationship, "name1");
        when(mockHyperResource.getLinks(relationship)).thenReturn(
            new HyperLink[]{
                link1,
                makeLink(relationship, "name2"),
                makeLink(relationship, "name3")
            }
        );
        when(mockHyperResource.getLink(relationship, "name1")).thenReturn(link1);

        HyperLink result = r.firstLinkTestNameThenWildCard();
        assertEquals(link1, result);
    }


    @Test
    public void testGetFirstLinkUsingMatchTestThenWildCard() {
        String relationship = "x:first-link";

        FirstLinkResource r = this.getHyperResourceProxy(FirstLinkResource.class);

        HyperLink link1 = makeLink(relationship, "test");
        when(mockHyperResource.getLinks(relationship)).thenReturn(
            new HyperLink[]{
                makeLink(relationship, "name2"),
                link1,
                makeLink(relationship, "name3")
            }
        );
        when(mockHyperResource.getLink(relationship, "test")).thenReturn(link1);

        HyperLink result = r.firstLinkTestNameThenWildCard();
        assertEquals(link1, result);
    }


    @Test
    public void testGetFirstLinkWithNoMatchThenEmptyMatching() {
        String relationship = "x:first-link";

        FirstLinkResource r = this.getHyperResourceProxy(FirstLinkResource.class);

        HyperLink link1 = makeLink(relationship, "");
        when(mockHyperResource.getLinks(relationship)).thenReturn(
            new HyperLink[]{
                makeLink(relationship, null),
                makeLink(relationship, "name3"),
                link1,
            }
        );
        when(mockHyperResource.getLink(relationship, "")).thenReturn(link1);

        HyperLink result = r.firstLinkTestNameThenEmptyName();
        assertEquals(link1, result);
    }

    @Test
    public void testGetFirstLinkWithNoMatchThenNullMatching() {
        String relationship = "x:first-link";

        FirstLinkResource r = this.getHyperResourceProxy(FirstLinkResource.class);

        HyperLink link1 = makeLink(relationship, null);
        when(mockHyperResource.getLinks(relationship)).thenReturn(
            new HyperLink[]{
                makeLink(relationship, ""),
                makeLink(relationship, "name3"),
                link1,
            }
        );
        when(mockHyperResource.getLink(relationship, null)).thenReturn(link1);

        HyperLink result = r.firstLinkTestNameThenNullName();
        assertEquals(link1, result);
    }

    @Test
    public void testFormAnnotatedMethodReturningBoolean(){
        FormResource r = this.getHyperResourceProxy(FormResource.class);

        when(mockHyperResource.hasForm("FormA"))
            .thenReturn(true);

        when(mockHyperResource.hasForm("FormB"))
            .thenReturn(false);

        assertTrue(r.hasFormA());

        assertFalse(r.hasFormB());

    }


    @Test
    public void testFormAnnotatedMethodReturningForm(){
        FormResource r = this.getHyperResourceProxy(FormResource.class);

        Form expected = mock(Form.class);

        when(mockHyperResource.getForm("FormA"))
            .thenReturn(expected);

        Form actual = r.getFormA();

        assertSame(expected, actual);

    }


    @Test
    public void testFormAnnotatedMethodSubmittingFormNoParams(){
        FormResource r = this.getHyperResourceProxy(FormResource.class);


        Form mockForm = mock(Form.class);

        when(mockHyperResource.getForm("FormA"))
            .thenReturn(mockForm);

        RequestBuilder mockRequestBuilder = mock(RequestBuilder.class);

        when(mockForm.toRequestBuilder())
            .thenReturn(mockRequestBuilder);

        FormResource expectedResult = mock(FormResource.class);

        when(mockHyperfitProcessor.processRequest(eq(FormResource.class), eq(mockRequestBuilder), any(TypeInfo.class)))
            .thenReturn(expectedResult);

        FormResource actual = r.formAResource();

        assertSame(expectedResult, actual);

    }


    @Test
    public void testFormAnnotatedMethodSubmittingFormWithParams(){
        FormResource r = this.getHyperResourceProxy(FormResource.class);


        Form mockForm = mock(Form.class);

        when(mockHyperResource.getForm("FormA"))
            .thenReturn(mockForm);

        RequestBuilder mockRequestBuilder = mock(RequestBuilder.class);

        when(mockForm.toRequestBuilder())
            .thenReturn(mockRequestBuilder);

        FormResource expectedResult = mock(FormResource.class);

        when(mockHyperfitProcessor.processRequest(eq(FormResource.class), eq(mockRequestBuilder), any(TypeInfo.class)))
            .thenReturn(expectedResult);

        String value1 = uniqueString();
        FormResource actual = r.formAResource(
            value1,
            CheckboxField.CheckState.CHECKED
        );

        verify(mockRequestBuilder).setParam("Field1", value1);
        verify(mockRequestBuilder).setParam("Field2", CheckboxField.CheckState.CHECKED);

        assertSame(expectedResult, actual);

    }

    @Test
    public void testInvokeMissingNodeWithNullWhenMissing() {

        DataResource proxyTest = getHyperResourceProxy(DataResource.class);

        assertNull(proxyTest.iCanBeNull());
    }

    @Test
    public void testInvokeFollowMissingLink() {

        DataResource proxyTest = getHyperResourceProxy(DataResource.class);

        assertNull(proxyTest.followNullWhenMissingLink());
    }
    @Test
    public void testInvokeFollowMissingNamedLink() {

        DataResource proxyTest = getHyperResourceProxy(DataResource.class);

        assertNull(proxyTest.followNullWhenMissingNamedLink());
    }



    @Test
    public void testInvokeGetMissingLink() {

        DataResource proxyTest = getHyperResourceProxy(DataResource.class);

        assertNull(proxyTest.getNullWhenMissingLink());
    }
    @Test
    public void testInvokeGetMissingNamedLink() {

        DataResource proxyTest = getHyperResourceProxy(DataResource.class);

        assertNull(proxyTest.getNullWhenMissingNamedLink());
    }


    @Test
    public void testInvokeGetMissingLinkArray() {

        DataResource proxyTest = getHyperResourceProxy(DataResource.class);

        assertNull(proxyTest.getNullWhenMissingLinkArray());
    }
    @Test
    public void testInvokeGetMissingNamedLinkArray() {

        DataResource proxyTest = getHyperResourceProxy(DataResource.class);

        assertNull(proxyTest.getNullWhenMissingNamedLinkArray());
    }

}
