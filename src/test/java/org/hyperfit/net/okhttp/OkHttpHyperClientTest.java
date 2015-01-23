package org.hyperfit.net.okhttp;

import org.hyperfit.exception.HyperfitException;
import org.hyperfit.net.*;
import com.google.common.base.Charsets;
import com.squareup.okhttp.*;

import java.net.CookieManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import okio.Buffer;
import okio.BufferedSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.mockito.Mockito.*;



public class OkHttpHyperClientTest {

    private static final String CONTENT_TYPE = "application/json; ; charset=utf-8";
    private final static String ACCEPT_VALUE = "application/hal+json";
    private static final String CONTENT_BODY = "{test:\"test\"}";
    private final static String URL = "http://api-cloud-01.qa:8080/commerce-hyper-api/";
    private final static Map<String, String> HEADERS = new HashMap<String, String>() {
        {
            put(HttpHeader.ACCEPT, ACCEPT_VALUE);
        }
    };

    @Mock
    private org.hyperfit.net.Request hcRequestMock;


    @Mock
    private OkHttpClientShim mockOkHttpClient;


    private OkHttpHyperClient okHttpHyperClient;



    private Request.Builder okRequestBuilder;
    private Response.Builder okResponseBuilder;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        okHttpHyperClient = new OkHttpHyperClient(this.mockOkHttpClient);
        HashSet<String> mediaTypeSet = new HashSet<String>();
        mediaTypeSet.add(ACCEPT_VALUE);
        okHttpHyperClient.setAcceptedContentTypes(mediaTypeSet);

        okRequestBuilder = new Request.Builder();
        okResponseBuilder = new Response.Builder();

    }

    @Test(expected = NullPointerException.class)
    public void testExecuteNullRequest() {
        okHttpHyperClient.execute(null);
    }

    @Test(expected = NullPointerException.class)
    public void testExecuteNullMethod() {

        when(hcRequestMock.getMethod()).thenReturn(null);
        okHttpHyperClient.execute(hcRequestMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteEmptyUrl() {


        when(hcRequestMock.getMethod()).thenReturn(Method.GET);
        when(hcRequestMock.getUrl()).thenReturn("");
        okHttpHyperClient.execute(hcRequestMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteNullUrl() {
        when(hcRequestMock.getMethod()).thenReturn(Method.GET);
        when(hcRequestMock.getUrl()).thenReturn(null);
        okHttpHyperClient.execute(hcRequestMock);
    }

    @Test(expected = NullPointerException.class)
    public void testNullClient() {
        OkHttpClient client = null;
        new OkHttpHyperClient(client);
    }

    @Test
    public void testExecuteAddsRequestBody() throws Exception {


        org.hyperfit.net.Request requestFake = org.hyperfit.net.Request.builder()
            .setUrlTemplate(URL)
            .setMethod(Method.POST)
            .setContentBody(CONTENT_BODY)
            .setContentType(CONTENT_TYPE)
            .build();


        com.squareup.okhttp.Request request = okHttpHyperClient.prepareRequest(requestFake);
        assertEquals(CONTENT_TYPE, request.body().contentType().toString());
        assertEquals(CONTENT_BODY.length(), request.body().contentLength());
    }

    @Test
    public void testExecuteOmitsRequestBody() throws Exception {
        org.hyperfit.net.Request requestFake = org.hyperfit.net.Request.builder()
            .setUrlTemplate(URL)
            .setMethod(Method.GET)
            .build();


        com.squareup.okhttp.Request request = okHttpHyperClient.prepareRequest(requestFake);
        assertNull(request.body());
    }

    @Test
    public void testRequestMethods() throws Exception {
        for(Method m : Method.values()){
            org.hyperfit.net.Request requestFake = org.hyperfit.net.Request.builder()
                .setUrlTemplate(URL)
                .setMethod(m)
                .build();

            com.squareup.okhttp.Request request = okHttpHyperClient.prepareRequest(requestFake);
            assertEquals(m.toString(), request.method());

        }

    }


    @Test
    public void testExecuteAddsHeaders() throws Exception {

        org.hyperfit.net.Request requestFake = org.hyperfit.net.Request.builder()
            .setUrlTemplate(URL)
            .setMethod(Method.GET)
            .build();


        com.squareup.okhttp.Request request = okHttpHyperClient.prepareRequest(requestFake);
        assertEquals(HEADERS.get(HttpHeader.ACCEPT), request.header(HttpHeader.ACCEPT));
    }

    @Test
    public void testExecuteOmitsHeaders() throws Exception {

        org.hyperfit.net.Request requestFake = org.hyperfit.net.Request.builder()
            .setUrlTemplate(URL)
            .setMethod(Method.GET)
            .build();

        com.squareup.okhttp.Request request = okHttpHyperClient.prepareRequest(requestFake);
        assertEquals(1, request.headers().size());
    }

    @Test
    public void testDoResponse() throws Exception {

        Request okRequestFake = okRequestBuilder
            .url(URL)
            .build();

        String fakeHeader = UUID.randomUUID().toString();
        String fakeHeaderValue = UUID.randomUUID().toString();

        String fakeContent = UUID.randomUUID().toString();

        final Buffer fakeBuffer = new Buffer();
        fakeBuffer.writeString(fakeContent, Charsets.UTF_8);

        Response okResponseFake = okResponseBuilder
            .request(okRequestFake)
            .code(200)
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
            .header(fakeHeader, fakeHeaderValue)
            .build();



        org.hyperfit.net.Response response = okHttpHyperClient.doResponse(okResponseFake);

        assertEquals(200, response.getCode());
        assertEquals(fakeContent, response.getBody());
        assertEquals(fakeHeaderValue, response.getHeader(fakeHeader));
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

        org.hyperfit.net.Response response = okHttpHyperClient.doResponse(okResponseFake);

        assertFalse(response.getHeaders().hasNext());
    }


    @Test(expected = HyperfitException.class)
    public void testExecuteResponseUnreadable() throws Exception {

        Request okRequestFake = okRequestBuilder
            .url(URL)
            .build();


        Response okResponseFake = okResponseBuilder
            .request(okRequestFake)
            .code(200)
            .protocol(Protocol.HTTP_1_1)
            .body(null)
            .build();


        okHttpHyperClient.doResponse(okResponseFake);

    }

    @Test
    public void testSetCookieHandler() throws Exception {
        CookieManager handler = new CookieManager();

        HyperClient result = okHttpHyperClient.setCookieHandler(handler);
        assertSame("setCookieHandler should be fluent", okHttpHyperClient, result);
        verify(mockOkHttpClient, times(1)).setCookieHandler(handler);

        okHttpHyperClient.setCookieHandler(null);
        verify(mockOkHttpClient, times(1)).setCookieHandler(null);

    }

}
