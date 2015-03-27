package org.hyperfit.net;

import java.util.Map;
import java.util.UUID;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


public class ResponseTest {

    @Mock
    protected Request mockRequest;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCode() {
        Response response = Response.builder()
            .addRequest(mockRequest)
            .addCode(500)
            .build();

        assertEquals(response.getCode(), 500);
    }

    @Test
    public void testGetBody() {
        Response response = Response.builder()
            .addRequest(mockRequest)
            .addBody("{}")
            .build();

        assertEquals(response.getBody(), "{}");
    }

    @Test
    public void testGetBodyNull() {
        Response response = Response.builder()
            .addRequest(mockRequest)
            .addBody(null)
            .build();

        assertEquals(response.getBody(), null);
    }

    @Test
    public void testGetHeaders() {
        String header1Name = UUID.randomUUID().toString();
        String header1Value = UUID.randomUUID().toString();

        String header2Name = UUID.randomUUID().toString();
        String header2Value = UUID.randomUUID().toString();

        Response response = Response.builder()
            .addRequest(mockRequest)
            .addHeader(header1Name, header1Value)
            .addHeader(header2Name, header2Value)
            .build();

        ImmutableList<String> keys = FluentIterable.from(response.getHeaders())
        .transform(new Function<Map.Entry<String, String>, String>() {
            public String apply(Map.Entry<String, String> input) {
                return input.getKey();
            }
        })
        .toList();

        assertThat(keys, containsInAnyOrder(header1Name, header2Name));

        ImmutableList<String> values = FluentIterable.from(response.getHeaders())
        .transform(new Function<Map.Entry<String, String>, String>() {
            public String apply(Map.Entry<String, String> input) {
                return input.getValue();
            }
        })
        .toList();

        assertThat(values, containsInAnyOrder(header1Value, header2Value));

    }

    @Test
    public void testGetHeadersNullHeader() {
        String header1Name = UUID.randomUUID().toString();
        String header1Value = UUID.randomUUID().toString();

        String header2Name = UUID.randomUUID().toString();
        String header2Value = null;

        Response response = Response.builder()
            .addRequest(mockRequest)
            .addHeader(header1Name, header1Value)
            .addHeader(header2Name, header2Value)
            .build();

        ImmutableList<String> keys = FluentIterable.from(response.getHeaders())
        .transform(new Function<Map.Entry<String, String>, String>() {
            public String apply(Map.Entry<String, String> input) {
                return input.getKey();
            }
        })
        .toList();

        assertThat(keys, contains(header1Name));

        ImmutableList<String> values = FluentIterable.from(response.getHeaders())
        .transform(new Function<Map.Entry<String, String>, String>() {
            public String apply(Map.Entry<String, String> input) {
                return input.getValue();
            }
        })
        .toList();

        assertThat(values, contains(header1Value));
    }

    @Test
    public void testGetHeader() {
        String header1Name = UUID.randomUUID().toString();
        String header1Value = UUID.randomUUID().toString();

        String header2Name = UUID.randomUUID().toString();
        String header2Value = UUID.randomUUID().toString();

        Response response = Response.builder()
            .addRequest(mockRequest)
            .addHeader(header1Name, header1Value)
            .addHeader(header2Name, header2Value)
            .build();

        assertEquals(header1Value, response.getHeader(header1Name));
    }

    @Test
    public void testGetHeaderNullValue() {
        String header1Name = UUID.randomUUID().toString();
        String header1Value = UUID.randomUUID().toString();

        String header2Name = UUID.randomUUID().toString();
        String header2Value = null;

        Response response = Response.builder()
            .addRequest(mockRequest)
            .addHeader(header1Name, header1Value)
            .addHeader(header2Name, header2Value)
            .build();

        assertNull(response.getHeader(header2Name));



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
