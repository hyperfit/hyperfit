package org.hyperfit.net.okhttp1;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.net.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.util.*;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class OkHttp1HyperClientTest {



    OkHttpClient okHttpClient;

    OkHttp1HyperClient client;

    @Before
    public void setUp() throws Exception {
        okHttpClient = new OkHttpClient();
        client = spy(new OkHttp1HyperClient(okHttpClient));
    }

    @Test
    public void testExecute() throws Exception {

    }

    @Test
    public void testExecute_WhenRequestNull() throws Exception {


        try{
            client.execute(null);
            fail("expected exception not thrown");
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("request cannot be null"));
        }


    }

    @Test
    public void testExecute_WhenMethodNull() throws Exception {
        Request request = mock(Request.class);
        when(request.getMethod()).thenReturn(null);


        try{
            client.execute(request);
            fail("expected exception not thrown");
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("request's method cannot be null"));
        }

    }

    @Test
    public void testExecute_WhenRequestEmpty() throws Exception {

        Request request = mock(Request.class);
        when(request.getMethod()).thenReturn(Method.CONNECT);

        try{
            client.execute(request);
            fail("expected exception not thrown");
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("request's url cannot be empty"));
        }
    }


    @Test
    public void testExecute_OnIOException() throws Exception {

        Request request = mock(Request.class);
        when(request.getMethod()).thenReturn(Method.CONNECT);
        when(request.getUrl()).thenReturn("foo");

        final IOException exception = new IOException("FooBar");
        doThrow(exception).when(client).openConnection(request);


        try{
            client.execute(request);
            fail("expected exception not thrown");
        }catch(HyperfitException e){
            assertSame(exception, e.getCause());
        }

    }

    @Test
    public void testSetCookieHandler() throws Exception {
        CookieHandler handler = mock(CookieHandler.class);

        client.setCookieHandler(handler);
        assert okHttpClient.getCookieHandler() == handler;


    }

    @Test
    public void testOpenConnection() throws Exception {
        MockWebServer server = new MockWebServer();
        server.play();

        String url = server.getUrl("/foo").toString();

        Request request = new BoringRequestBuilder()
                            .setUrl(url)
                            .build();

        HttpURLConnection connection = client.openConnection(request);

        String actualUrl = connection.getURL().toString();
        assertEquals(url, actualUrl);

    }

    @Test
    public void testPrepareRequest() throws Exception {
        Request request = new BoringRequestBuilder()
                .setUrl("/Foo")
                .setMethod(Method.CONNECT)
                .setContentType("text/html")
                .setContent("MY CONTENT")
                .build();

        doReturn(OkHttp1HyperClient.UTF_8).when(client).charset(request.getContentType());

        OutputStream stream = mock(OutputStream.class);
        HttpURLConnection connection = mock(HttpURLConnection.class);
        doNothing().when(client).prepareHeaders(connection, request);
        when(connection.getOutputStream()).thenReturn(stream);

        client.prepareRequest(connection, request);

        verify(connection).setRequestMethod(request.getMethod().name());
        verify(connection).setDoInput(true);
        verify(client).prepareHeaders(connection, request);
        verify(client).charset(request.getContentType());

        byte [] body = request.getContent().getBytes(OkHttp1HyperClient.UTF_8);
        verify(connection).setFixedLengthStreamingMode(body.length);

        verify(stream).write(body);

    }

    @Test
    public void testPrepareRequest_WhenContentTypeEmpty() throws Exception {

        Request request = mock(Request.class);
        when(request.getMethod()).thenReturn(Method.CONNECT);
        when(request.getContent()).thenReturn("foo");


        HttpURLConnection connection = mock(HttpURLConnection.class);
        doNothing().when(client).prepareHeaders(connection, request);

        client.prepareRequest(connection, request);

        verify(connection).setRequestMethod(request.getMethod().name());
        verify(connection).setDoInput(true);
        verify(client).prepareHeaders(connection, request);
        verify(connection, never()).getOutputStream();

    }

    @Test
    public void testPrepareRequest_WhenContentEmpty() throws Exception {
        Request request = mock(Request.class);
        when(request.getMethod()).thenReturn(Method.CONNECT);
        when(request.getContentType()).thenReturn("text/html");


        HttpURLConnection connection = mock(HttpURLConnection.class);
        doNothing().when(client).prepareHeaders(connection, request);

        client.prepareRequest(connection, request);

        verify(connection).setRequestMethod(request.getMethod().name());
        verify(connection).setDoInput(true);
        verify(client).prepareHeaders(connection, request);
        verify(connection, never()).getOutputStream();

    }

    @Test
    public void testPrepareHeaders() throws Exception {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("entry1", "value1");
        headerMap.put("entry2", "value2");
        headerMap.put("entry3", "value3");

        Request request = mock(Request.class);
        when(request.getHeaders()).thenReturn(headerMap.entrySet());
        HttpURLConnection connection = mock(HttpURLConnection.class);

        client.prepareHeaders(connection, request);

        for (Map.Entry<String, String> entry: headerMap.entrySet()){
            verify(connection).addRequestProperty(entry.getKey(), entry.getValue());
        }

    }

    @Test
    public void testReadResponse() throws Exception {

        HttpURLConnection connection = mock(HttpURLConnection.class);

        Request mockRequest = mock(Request.class);

        String body = "body";

        doNothing().when(client).addHeadersToResponse(eq(connection), Matchers.any(Response.ResponseBuilder.class));
        doReturn(body).when(client).convertResponseBodyToString(connection);

        int returnCode = 100;
        when(connection.getResponseCode()).thenReturn(returnCode);
        String contentType = "bar/foo";
        when(connection.getContentType()).thenReturn(contentType);

        Response actual = client.readResponse(connection, mockRequest);

        assertNotNull(actual);
        assertEquals(returnCode, actual.getCode());
        assertEquals(contentType, actual.getContentType());
        assertEquals(body, actual.getBody());
        assertEquals(mockRequest, actual.getRequest());

    }

    @Test
    public void testConvertResponseBodyToString() throws Exception {
        HttpURLConnection connection = mock(HttpURLConnection.class);

        String contentType = "foo";
        when(connection.getContentType()).thenReturn(contentType);

        String body = "Hello World";
        InputStream is = new ByteArrayInputStream(body.getBytes());
        doReturn(is).when(client).getResponseInputStream(connection);
        doReturn(OkHttp1HyperClient.UTF_8).when(client).charset(contentType);


        String actual = client.convertResponseBodyToString(connection);
        assertEquals(body, actual);


    }

    @Test
    public void testGetResponseInputStream() throws Exception {
        HttpURLConnection connection = mock(HttpURLConnection.class);
        InputStream is = mock(InputStream.class);
        InputStream isError = mock(InputStream.class);
        when(connection.getInputStream()).thenReturn(is);
        when(connection.getErrorStream()).thenReturn(isError);

        for (int i = 0 ; i < 400 ;i++) {
            when(connection.getResponseCode()).thenReturn(i);
            InputStream actual = client.getResponseInputStream(connection);
            assertSame(is, actual);
        }

        for (int i = 400 ; i < 1000 ;i++) {
            when(connection.getResponseCode()).thenReturn(i);
            InputStream actual = client.getResponseInputStream(connection);
            assertSame(isError, actual);
        }
    }





    @Test
    public void testAddHeadersToResponse() throws Exception {
        Request mockRequest = mock(Request.class);


        Response.ResponseBuilder responseBuilder = Response.builder()
                .addCode(500)
                .addRequest(mockRequest)
                .addContentType("text/html")
                .addBody("foo");

        Map<String, List<String>> headerMap = new HashMap<String, List<String>>();
        headerMap.put("entry1", Collections.singletonList("value1"));
        headerMap.put("entry2", Collections.singletonList("value2"));
        headerMap.put("entry3", Lists.newArrayList("value3", "value4"));

        HttpURLConnection connection = mock(HttpURLConnection.class);
        when(connection.getHeaderFields()).thenReturn(headerMap);

        client.addHeadersToResponse(connection, responseBuilder);


        Response response = responseBuilder.build();
        Map<String, String> headers = Maps.newHashMap();
        for (Iterator<Map.Entry<String, String>> responseHeaders = response.getHeaders().iterator(); responseHeaders.hasNext(); ){
            Map.Entry<String, String> header = responseHeaders.next();
            headers.put(header.getKey(), header.getValue());
        }

        assertThat(
            headers,
            allOf(
                hasEntry("entry1", "value1"),
                hasEntry("entry2", "value2"),
                hasEntry("entry3", "value4")
            )
        );
    }

    @Test
    public void testGetSchemes(){
        String[] expected = new String[]{"http", "https"};
        String[] real = client.getSchemes();
        Arrays.sort(expected);
        Arrays.sort(real);
        Arrays.equals(expected,real);
    }
}