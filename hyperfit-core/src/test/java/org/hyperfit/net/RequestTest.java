package org.hyperfit.net;


import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


import org.hyperfit.net.HttpHeader;
import org.junit.Test;


public class RequestTest {


    public int count(Iterator it) {
        int count = 0;

        while (it.hasNext()) {
            count++;
        }

        return count;
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetUrlTemplateNull() {
        Request.builder().setUrlTemplate(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUrlTemplateEmpty() {
        Request.builder().setUrlTemplate("").build();
    }

    @Test
    public void testGetUrlNonTemplated() {

        String actual = Request.builder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/")
                .build()
                .getUrl();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/", actual);
    }

    @Test
    public void testGetUrlTemplated() {

        String actual = Request.builder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
                        //Note we don't set a param
                .build()
                .getUrl();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/", actual);
    }

    @Test
    public void testGetUrlExpand() {

        String actual = Request.builder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
                .setUrlParam("param", "1")
                .build()
                .getUrl();


        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/?param=1", actual);
    }

    @Test
    public void testGetUrlExpandNullParam() {

        String actual = Request.builder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
                .setUrlParam("param", null)
                .build()
                .getUrl();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/", actual);
    }

    @Test
    public void testGetUrlExpandEmptyParam() {

        String actual = Request.builder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
                .setUrlParam("param", "")
                .build()
                .getUrl();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/?param=", actual);
    }

    @Test
    public void testGetUrlExpandNonExistentParamParam() {
        //If a user submits extra params then we should just ignore them!
        //This is useful in the cases where a param exists sometimes, but sometimes the api just provides the values
        //and thus the client can't set the parameter.

        String actual = Request.builder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
                .setUrlParam("paramNotThere", "")
                .build()
                .getUrl();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/", actual);
    }




    @Test
    public void testGetUrlTemplate() {

        String actual = Request.builder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/")
                .build()
                .getUrlTemplate();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/", actual);
    }

    @Test
    public void testGetUrlTemplateExpand() {
        String actual = Request.builder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
                .setUrlParam("param", "1")
                .build()
                .getUrlTemplate();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}", actual);

    }

    @Test
    public void testGetUrlTemplateExpandNullParam() {
        String actual = Request.builder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
                .setUrlParam("param", null)
                .build()
                .getUrlTemplate();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}", actual);

    }

    @Test
    public void testGetUrlTemplateExpandEmptyParam() {
        String actual = Request.builder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
                .setUrlParam("param", "")
                .build()
                .getUrlTemplate();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}", actual);


    }

    @Test
    public void testGetContentBody() {
        assertEquals(Request.builder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
        setContent("{test:\"ok\"}").build().getContent(),
                "{test:\"ok\"}");
    }

    @Test(expected = NullPointerException.class)
    public void testGetContentBodyNull() {
        Request.builder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
        setContent(null).build().getContent();
    }

    @Test
    public void testGetContentType() {
        assertEquals(Request.builder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
        setContent("application/json").build().getContent(),
                "application/json");
    }

    @Test(expected = NullPointerException.class)
    public void testGetContentTypeNull() {
        Request.builder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
        setContent(null).build().getContent();
    }

    @Test
    public void testGetMethod() {
        assertEquals(Request.builder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                        setMethod(Method.GET).build().getMethod(),
                Method.GET);
    }

    @Test(expected = NullPointerException.class)
    public void testGetMethodNull() {
        Request.builder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                setMethod(null).build().getMethod();
    }

    @Test
    public void testGetUrlParam() {
        assertEquals(Request.builder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}").
                setUrlParam("param", "1").build().getUrlParam("param"),
                "1");
    }

    @Test
    public void testGetUrlParamNullValue() {
        assertEquals(Request.builder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}").
                setUrlParam("param", null).build().getUrlParam("param"),
                null);
        assertEquals(count(Request.builder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}").
                setUrlParam("param", null).build().getUrlParams()),
                0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUrlParamNullKey() {
        Request.builder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}").
            setUrlParam("", "1").build().getUrlParam("param");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUrlParamEmptyKey() {
        Request.builder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}").
            setUrlParam(null, "1").build().getUrlParam("param");
    }

    @Test
    public void testGetHeader() {
        assertEquals(Request.builder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                        addHeader(HttpHeader.ACCEPT, "application/json").build().getHeader(HttpHeader.ACCEPT),
                "application/json");
    }

    @Test
    public void testGetHeaderNullValue() {
        assertEquals(Request.builder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                        addHeader(HttpHeader.ACCEPT, null).build().getHeader(HttpHeader.ACCEPT),
                null);
        assertEquals(count(Request.builder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                        addHeader(HttpHeader.ACCEPT, null).build().getHeaders()),
                0);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetHeaderNullKey() {
        Request.builder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                addHeader("", "application/json").build().getHeader("param");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetHeaderEmptyKey() {
        Request.builder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                addHeader(null, "application/json").build().getHeader("param");
    }

    @Test
    public void testGetUrlParams() {
        Request request = Request.builder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param1,param2}").
            setUrlParam("param1", "1").
            setUrlParam("param2", "2").
                build();

        int count = 0;
        for (Iterator<Map.Entry<String, String>> it = request.getUrlParams(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();

            if (entry.getKey().equals("param1")) {
                assertEquals(entry.getValue(), "1");
                count++;
            }

            if (entry.getKey().equals("param2")) {
                assertEquals(entry.getValue(), "2");
                count++;
            }
        }
        assertEquals(count, 2);
    }

    @Test
    public void testGetUrlParamsNullParam() {
        Request request = Request.builder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param1,param2}").
            setUrlParam("param1", "1").
            setUrlParam("param2", null).
                build();

        int count = 0;
        for (Iterator<Map.Entry<String, String>> it = request.getUrlParams(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();

            if (entry.getKey().equals("param1")) {
                assertEquals(entry.getValue(), "1");
                count++;
            }

            if (entry.getKey().equals("param2")) {
                count++;
            }
        }
        assertEquals(count, 1);
    }

    @Test
    public void testGetHeaders() {
        Request request = Request.builder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                addHeader(HttpHeader.ACCEPT, "application/json").
                addHeader(HttpHeader.CONTENT_TYPE, "text/html").
                build();

        int count = 0;
        for (Iterator<Map.Entry<String, String>> it = request.getHeaders(); it.hasNext();) {
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
        Request request = Request.builder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                addHeader(HttpHeader.ACCEPT, "application/json").
                addHeader(HttpHeader.CONTENT_TYPE, null).
                build();

        int count = 0;
        for (Iterator<Map.Entry<String, String>> it = request.getHeaders(); it.hasNext();) {
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
    public void testGet() {
        assertEquals(Request.get("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getMethod(),
                Method.GET);
        assertEquals(Request.get("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getUrlTemplate(),
                "http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}");
    }

    @Test
    public void testPost() {
        assertEquals(Request.post("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getMethod(),
                Method.POST);
        assertEquals(Request.post("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getUrlTemplate(),
                "http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}");
    }

    @Test
    public void testPut() {
        assertEquals(Request.put("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getMethod(),
                Method.PUT);
        assertEquals(Request.put("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getUrlTemplate(),
                "http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}");
    }

    @Test
    public void testDelete() {
        assertEquals(Request.delete("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getMethod(),
                Method.DELETE);
        assertEquals(Request.delete("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getUrlTemplate(),
                "http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}");
    }

    @Test
    public void testOptions() {
        assertEquals(Request.options("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getMethod(),
                Method.OPTIONS);
        assertEquals(Request.options("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getUrlTemplate(),
                "http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}");
    }

    @Test
    public void testHead() {
        assertEquals(Request.head("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getMethod(),
                Method.HEAD);
        assertEquals(Request.head("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getUrlTemplate(),
                "http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}");
    }

    @Test
    public void testTrace() {
        assertEquals(Request.trace("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getMethod(),
                Method.TRACE);
        assertEquals(Request.trace("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getUrlTemplate(),
                "http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}");
    }

    @Test
    public void testConnect() {
        assertEquals(Request.connect("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getMethod(),
                Method.CONNECT);
        assertEquals(Request.connect("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getUrlTemplate(),
                "http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}");
    }

    @Test
    public void testBuilder() {
        assertTrue(Request.builder() instanceof Request.RequestBuilder);
    }

    @Test
    public void testEquals(){

        String fakeUrl = UUID.randomUUID().toString();

        Request request1 = Request.builder()
                .setUrlTemplate(fakeUrl)
                .build();

        Request request2 = Request.builder()
                .setUrlTemplate(fakeUrl)
                .build();

        assertEquals(request1, request2);



        request2 = Request.builder()
                .setUrlTemplate(fakeUrl + "1")
                .build();

        assertFalse(request1.equals(request2));


        //TODO: a whole bunch more of these conditions!
    }



}
