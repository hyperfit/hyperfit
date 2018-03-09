package org.hyperfit.net.okhttp3;

import com.google.common.collect.ArrayListMultimap;
import okhttp3.*;
import org.hyperfit.exception.HyperfitException;

import com.google.common.base.Charsets;

import java.net.CookieHandler;
import java.util.*;

import static org.hyperfit.net.HttpUtils.ACCEPT;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import okio.Buffer;
import okio.BufferedSource;
import org.hyperfit.net.HyperClient;
import org.hyperfit.net.Method;
import org.hyperfit.net.RFC6570RequestBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.mockito.Mockito.*;



public class OkHttp3HyperClientTest {

    private static final String CONTENT_TYPE = "application/json; ; charset=utf-8";
    private final static String ACCEPT_VALUE = "application/hal+json";
    private static final String CONTENT_BODY = "{test:\"test\"}";
    private final static String URL = "http://api-cloud-01.qa:8080/commerce-hyper-api/";
    private final static Map<String, String> HEADERS = new HashMap<String, String>() {
        {
            put(ACCEPT, ACCEPT_VALUE);
        }
    };

    @Mock
    private org.hyperfit.net.Request hyperfitRequestMock;


    @Mock
    private OkHttpClient mockOkHttpClient;


    private OkHttp3HyperClient okHttp3HyperClient;



    private Request.Builder okRequestBuilder;
    private Response.Builder okResponseBuilder;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        okHttp3HyperClient = new OkHttp3HyperClient(this.mockOkHttpClient);
        HashSet<String> mediaTypeSet = new HashSet<String>();
        mediaTypeSet.add(ACCEPT_VALUE);
        okHttp3HyperClient.setAcceptedContentTypes(mediaTypeSet);

        okRequestBuilder = new Request.Builder();
        okResponseBuilder = new Response.Builder();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteNullRequest() {
        okHttp3HyperClient.execute(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteNullMethod() {

        when(hyperfitRequestMock.getMethod()).thenReturn(null);
        okHttp3HyperClient.execute(hyperfitRequestMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteEmptyUrl() {


        when(hyperfitRequestMock.getMethod()).thenReturn(Method.GET);
        when(hyperfitRequestMock.getUrl()).thenReturn("");
        okHttp3HyperClient.execute(hyperfitRequestMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteNullUrl() {
        when(hyperfitRequestMock.getMethod()).thenReturn(Method.GET);
        when(hyperfitRequestMock.getUrl()).thenReturn(null);
        okHttp3HyperClient.execute(hyperfitRequestMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullClient() {
        OkHttpClient client = null;
        new OkHttp3HyperClient(client);
    }

    @Test
    public void testExecuteAddsRequestBody() throws Exception {


        org.hyperfit.net.Request requestFake = new RFC6570RequestBuilder()
            .setUrlTemplate(URL)
            .setMethod(Method.POST)
            .setContent(CONTENT_BODY)
            .setContentType(CONTENT_TYPE)
            .build();


        Request request = okHttp3HyperClient.prepareRequest(requestFake);
        assertEquals(CONTENT_TYPE, request.body().contentType().toString());
        assertEquals(CONTENT_BODY.length(), request.body().contentLength());
    }

    @Test
    public void testExecuteOmitsRequestBody() throws Exception {
        org.hyperfit.net.Request requestFake = new RFC6570RequestBuilder()
            .setUrlTemplate(URL)
            .setMethod(Method.GET)
            .build();


        Request request = okHttp3HyperClient.prepareRequest(requestFake);
        assertNull(request.body());
    }

    @Test
    public void testRequestMethods() throws Exception {
        for(Method m : Method.values()){
            org.hyperfit.net.Request requestFake = new RFC6570RequestBuilder()
                .setUrlTemplate(URL)
                .setMethod(m)
                .build();

            Request request = okHttp3HyperClient.prepareRequest(requestFake);
            assertEquals(m.toString(), request.method());

        }

    }


    @Test
    public void testExecuteAddsHeaders() throws Exception {

        org.hyperfit.net.Request requestFake = new RFC6570RequestBuilder()
            .setUrlTemplate(URL)
            .setMethod(Method.GET)
            .build();


        Request request = okHttp3HyperClient.prepareRequest(requestFake);
        assertEquals(HEADERS.get(ACCEPT), request.header(ACCEPT));
    }

    @Test
    public void testExecuteOmitsHeaders() throws Exception {

        org.hyperfit.net.Request requestFake = new RFC6570RequestBuilder()
            .setUrlTemplate(URL)
            .setMethod(Method.GET)
            .build();

        Request request = okHttp3HyperClient.prepareRequest(requestFake);
        assertEquals(1, request.headers().size());
    }

    @Test
    public void testDoResponse() throws Exception {

        Request okRequestFake = okRequestBuilder
            .url(URL)
            .build();

        String fakeHeader = UUID.randomUUID().toString();
        String fakeHeaderValue1 = UUID.randomUUID().toString();
        String fakeHeaderValue2 = UUID.randomUUID().toString();

        String fakeContent = UUID.randomUUID().toString();

        final Buffer fakeBuffer = new Buffer();
        fakeBuffer.writeString(fakeContent, Charsets.UTF_8);

        Response okResponseFake = okResponseBuilder
            .request(okRequestFake)
            .code(200)
            .message("ok")
            .protocol(Protocol.HTTP_1_1)
            .body(new ResponseBody() {
                @Override
                public MediaType contentType() {
                    return null;
                }

                @Override
                public long contentLength() {
                    return 36;
                }

                @Override
                public BufferedSource source() {
                    return fakeBuffer;
                }
            })
            .addHeader(fakeHeader, fakeHeaderValue1)
            .addHeader(fakeHeader, fakeHeaderValue2)
            .build();



        org.hyperfit.net.Response response = okHttp3HyperClient.doResponse(okResponseFake, hyperfitRequestMock);

        assertEquals(200, response.getCode());
        assertEquals(fakeContent, response.getBody());
        ArrayListMultimap<Object,Object> expectedHeaders = ArrayListMultimap.create();

        expectedHeaders.put(fakeHeader, fakeHeaderValue1);
        expectedHeaders.put(fakeHeader, fakeHeaderValue2);

        //check that each collection contains each other
        assertTrue(expectedHeaders.entries().containsAll(response.getHeaders()));
        assertTrue(response.getHeaders().containsAll(expectedHeaders.entries()));


        assertSame(hyperfitRequestMock, response.getRequest());
    }

    @Test
    public void testExecuteResponseNoHeaders() throws Exception {
        Request okRequestFake = okRequestBuilder
            .url(URL)
            .build();


        String fakeContent = UUID.randomUUID().toString();

        final Buffer fakeBuffer = new Buffer();
        fakeBuffer.writeString(fakeContent, Charsets.UTF_8);

        Response okResponseFake = okResponseBuilder
            .request(okRequestFake)
            .code(200)
            .message("ok")
            .protocol(Protocol.HTTP_1_1)
            .body(new ResponseBody() {
                @Override
                public MediaType contentType() {
                    return null;
                }

                @Override
                public long contentLength() {
                    return 36;
                }

                @Override
                public BufferedSource source() {
                    return fakeBuffer;
                }
            })
            .build();

        org.hyperfit.net.Response response = okHttp3HyperClient.doResponse(okResponseFake, hyperfitRequestMock);

        assertFalse(response.getHeaders().iterator().hasNext());
        assertSame(hyperfitRequestMock, response.getRequest());
    }


    @Test(expected = HyperfitException.class)
    public void testExecuteResponseUnreadable() throws Exception {

        Request okRequestFake = okRequestBuilder
            .url(URL)
            .build();


        Response okResponseFake = okResponseBuilder
            .request(okRequestFake)
            .code(200)
            .message("ok")
            .protocol(Protocol.HTTP_1_1)
            .body(null)
            .build();


        okHttp3HyperClient.doResponse(okResponseFake, hyperfitRequestMock);

    }

    @Test
    public void testSetCookieHandler() throws Exception {
        CookieHandler mockHandler = mock(CookieHandler.class);

        HttpUrl fakeUrl = HttpUrl.parse("http://example.com");

        Map<String, List<String>> fakeCookies = new HashMap<String, List<String>>();
        fakeCookies.put("Cookie", Collections.singletonList("dog=cat"));

        when(mockHandler.get(fakeUrl.uri(), Collections.<String, List<String>>emptyMap()))
            .thenReturn(fakeCookies);

        OkHttpClient.Builder fakeBuilder = new OkHttpClient.Builder();

        when(mockOkHttpClient.newBuilder())
            .thenReturn(fakeBuilder);


        HyperClient result = okHttp3HyperClient.setCookieHandler(mockHandler);
        assertSame("setCookieHandler should be fluent", okHttp3HyperClient, result);

        CookieJar actualJar = fakeBuilder.build().cookieJar();

        List<Cookie> actual = actualJar.loadForRequest(fakeUrl);

        assertThat(
            actual,
            hasSize(1)
        );

        Cookie cookie = actual.get(0);

        assertEquals(
            "dog",
            cookie.name()
        );

        assertEquals(
            "cat",
            cookie.value()
        );

    }


    @Test
    public void testSetCookieHandlerNull() throws Exception {

        OkHttpClient.Builder fakeBuilder = new OkHttpClient.Builder();

        when(mockOkHttpClient.newBuilder())
            .thenReturn(fakeBuilder);


        HyperClient result = okHttp3HyperClient.setCookieHandler(null);
        assertSame("setCookieHandler should be fluent", okHttp3HyperClient, result);

        assertSame(
            CookieJar.NO_COOKIES,
            fakeBuilder.build().cookieJar()
        );

    }



    @Test
    public void testGetSchemes(){
        String[] expected = new String[]{"https", "http"};
        String[] real = okHttp3HyperClient.getSchemes();
        Arrays.sort(expected);
        Arrays.sort(real);
        Arrays.equals(expected,real);
    }
}
