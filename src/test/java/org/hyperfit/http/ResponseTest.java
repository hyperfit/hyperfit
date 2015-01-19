package org.hyperfit.http;

import java.util.Iterator;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class ResponseTest {


    public int count(Iterator it) {
        int count = 0;

        while (it.hasNext()) {
            count++;
        }

        return count;
    }

    @Test
    public void testGetCode() {
        assertEquals(Response.builder().addCode(500).build().getCode(), 500);
    }

    @Test
    public void testGetBody() {
        assertEquals(Response.builder().addBody("{}").build().getBody(), "{}");
    }

    @Test
    public void testGetBodyNull() {
        assertEquals(Response.builder().addBody(null).build().getBody(), null);
    }

    @Test
    public void testGetHeaders() {
        Response response = Response.builder().
                addHeader(HttpHeader.ACCEPT, "application/json").
                addHeader(HttpHeader.CONTENT_TYPE, "text/html").
                build();

        int count = 0;
        for (Iterator<Map.Entry<String, String>> it = response.getHeaders(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();

            if (entry.getKey().equals(HttpHeader.ACCEPT)) {
                assertEquals(entry.getValue(), "application/json");
                count++;
            }

            if (entry.getKey().equals(HttpHeader.CONTENT_TYPE)) {
                assertEquals(entry.getValue(), "text/html");
                count++;
            }
        }
        assertEquals(count, 2);
    }

    @Test
    public void testGetHeadersNullHeader() {
        Response response = Response.builder().
                addHeader(HttpHeader.ACCEPT, "application/json").
                addHeader(HttpHeader.CONTENT_TYPE, null).
                build();

        int count = 0;
        for (Iterator<Map.Entry<String, String>> it = response.getHeaders(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();

            if (entry.getKey().equals(HttpHeader.ACCEPT)) {
                assertEquals(entry.getValue(), "application/json");
                count++;
            }

            if (entry.getKey().equals(HttpHeader.CONTENT_TYPE)) {
                count++;
            }
        }
        assertEquals(count, 1);
    }

    @Test
    public void testGetHeader() {
        assertEquals(Response.builder().
                addHeader(HttpHeader.ACCEPT, "application/json").build().getHeader(HttpHeader.ACCEPT),
                "application/json");
    }

    @Test
    public void testGetHeaderNullValue() {
        assertEquals(Response.builder().
                addHeader(HttpHeader.ACCEPT, null).build().getHeader(HttpHeader.ACCEPT),
                null);
        assertEquals(count(Response.builder().
                addHeader(HttpHeader.ACCEPT, null).build().getHeaders()),
                0);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetHeaderNullKey() {
        Response.builder().
                addHeader("", "application/json").build().getHeader("param");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetHeaderEmptyKey() {
        Response.builder().
                addHeader(null, "application/json").build().getHeader("param");
    }

    @Test
    public void testBuilder() {
        assertTrue(Response.builder() instanceof Response.ResponseBuilder);
    }

}
