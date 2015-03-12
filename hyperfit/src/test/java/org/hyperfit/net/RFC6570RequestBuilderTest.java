package org.hyperfit.net;


import org.junit.Test;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class RFC6570RequestBuilderTest {


    public int count(Iterable i) {
        int count = 0;

        Iterator it = i.iterator();
        while (it.hasNext()) {
            count++;
        }

        return count;
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetUrlTemplateNull() {
        new RFC6570RequestBuilder().setUrlTemplate(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUrlTemplateEmpty() {
        new RFC6570RequestBuilder().setUrlTemplate("").build();
    }

    @Test
    public void testGetUrlNonTemplated() {

        String actual = new RFC6570RequestBuilder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/")
                .build()
                .getUrl();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/", actual);
    }

    @Test
    public void testGetUrlTemplated() {

        String actual = new RFC6570RequestBuilder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
                        //Note we don't set a param
                .build()
                .getUrl();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/", actual);
    }

    @Test
    public void testGetUrlExpand() {

        String actual = new RFC6570RequestBuilder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
                .setParam("param", "1")
                .build()
                .getUrl();


        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/?param=1", actual);
    }

    @Test
    public void testGetUrlExpandNullParam() {

        String actual = new RFC6570RequestBuilder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
                .setParam("param", null)
                .build()
                .getUrl();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/", actual);
    }

    @Test
    public void testGetUrlExpandEmptyParam() {

        String actual = new RFC6570RequestBuilder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
                .setParam("param", "")
                .build()
                .getUrl();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/?param=", actual);
    }

    @Test
    public void testGetUrlExpandNonExistentParamParam() {
        //If a user submits extra params then we should just ignore them!
        //This is useful in the cases where a param exists sometimes, but sometimes the api just provides the values
        //and thus the client can't set the parameter.

        String actual = new RFC6570RequestBuilder()
                .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
                .setParam("paramNotThere", "")
                .build()
                .getUrl();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/", actual);
    }




    @Test
    public void testGetUrlTemplate() {

        String actual = new RFC6570RequestBuilder()
            .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/")
            .build()
            .getUrl();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/", actual);
    }

    @Test
    public void testBuildURLWithValueForParam() {
        String actual = new RFC6570RequestBuilder()
            .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
            .setParam("param", "1")
            .getURL();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/?param=1", actual);

    }

    @Test
    public void testBuildURLNullParamValue() {
        String actual = new RFC6570RequestBuilder()
            .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
            .setParam("param", null)
            .getURL();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/", actual);

    }

    @Test
    public void testBuildURLEmptyParamValue() {
        String actual = new RFC6570RequestBuilder()
            .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
            .setParam("param", "")
            .getURL();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/?param=", actual);


    }

    @Test
    public void testGetContentBody() {
        assertEquals(new RFC6570RequestBuilder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
        setContent("{test:\"ok\"}").build().getContent(),
                "{test:\"ok\"}");
    }

    @Test(expected = NullPointerException.class)
    public void testGetContentBodyNull() {
        new RFC6570RequestBuilder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
        setContent(null).build().getContent();
    }

    @Test
    public void testGetContentType() {
        assertEquals(new RFC6570RequestBuilder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
        setContent("application/json").build().getContent(),
                "application/json");
    }

    @Test(expected = NullPointerException.class)
    public void testGetContentTypeNull() {
        new RFC6570RequestBuilder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
        setContent(null).build().getContent();
    }

    @Test
    public void testGetMethod() {
        assertEquals(new RFC6570RequestBuilder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                        setMethod(Method.GET).build().getMethod(),
                Method.GET);
    }

    @Test(expected = NullPointerException.class)
    public void testGetMethodNull() {
        new RFC6570RequestBuilder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                setMethod(null).build().getMethod();
    }

    @Test
    public void testgetParam() {
        RequestBuilder builder = new RFC6570RequestBuilder()
            .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}")
            .setParam("param", "1");
        
        assertEquals("1", builder.getParam("param"));
    }

    @Test
    public void testgetParamNullValue() {
        assertEquals(new RFC6570RequestBuilder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}").
        setParam("param", null).getParam("param"),
                null);
        assertEquals(count(new RFC6570RequestBuilder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}").
        setParam("param", null).getParams().keySet()),
                0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testgetParamNullKey() {
        new RFC6570RequestBuilder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}").
        setParam("", "1").getParam("param");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testgetParamEmptyKey() {
        new RFC6570RequestBuilder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}").
        setParam(null, "1").getParam("param");
    }

    @Test
    public void testGetHeader() {
        assertEquals(new RFC6570RequestBuilder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                        addHeader(HttpHeader.ACCEPT, "application/json").build().getHeader(HttpHeader.ACCEPT),
                "application/json");
    }

    @Test
    public void testGetHeaderNullValue() {
        assertEquals(new RFC6570RequestBuilder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                        addHeader(HttpHeader.ACCEPT, null).build().getHeader(HttpHeader.ACCEPT),
                null);
        assertEquals(count(new RFC6570RequestBuilder().
                        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                        addHeader(HttpHeader.ACCEPT, null).build().getHeaders()),
                0);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetHeaderNullKey() {
        new RFC6570RequestBuilder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                addHeader("", "application/json").build().getHeader("param");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetHeaderEmptyKey() {
        new RFC6570RequestBuilder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                addHeader(null, "application/json").build().getHeader("param");
    }

    @Test
    public void testGetParams() {
        RequestBuilder request = new RFC6570RequestBuilder()
            .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param1,param2}")
            .setParam("param1", "1")
            .setParam("param2", "2")
        ;

        int count = 0;
        for (Map.Entry<String,String> entry : request.getParams().entrySet()) {

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
    public void testGetParamsNullParam() {
        RequestBuilder request = new RFC6570RequestBuilder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param1,param2}").
        setParam("param1", "1").
        setParam("param2", null)
                ;

        int count = 0;
        for (Map.Entry<String,String> entry : request.getParams().entrySet()) {

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
        Request request = new RFC6570RequestBuilder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                addHeader(HttpHeader.ACCEPT, "application/json").
                addHeader(HttpHeader.CONTENT_TYPE, "text/html").
                build();

        int count = 0;
        for (Map.Entry<String,String> entry : request.getHeaders()) {

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
        Request request = new RFC6570RequestBuilder().
                setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/").
                addHeader(HttpHeader.ACCEPT, "application/json").
                addHeader(HttpHeader.CONTENT_TYPE, null).
                build();

        int count = 0;
        for (Map.Entry<String,String> entry : request.getHeaders()) {

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
        assertEquals(RFC6570RequestBuilder.get("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getMethod(),
                Method.GET);
        assertEquals(RFC6570RequestBuilder.get("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").getUrlTemplate(),
                "http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}");
    }

    @Test
    public void testPost() {
        assertEquals(RFC6570RequestBuilder.post("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getMethod(),
                Method.POST);
        assertEquals(RFC6570RequestBuilder.post("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").getUrlTemplate(),
                "http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}");
    }

    @Test
    public void testPut() {
        assertEquals(RFC6570RequestBuilder.put("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getMethod(),
                Method.PUT);
        assertEquals(RFC6570RequestBuilder.put("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").getUrlTemplate(),
                "http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}");
    }

    @Test
    public void testDelete() {
        assertEquals(RFC6570RequestBuilder.delete("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getMethod(),
                Method.DELETE);
        assertEquals(RFC6570RequestBuilder.delete("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").getUrlTemplate(),
                "http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}");
    }

    @Test
    public void testOptions() {
        assertEquals(RFC6570RequestBuilder.options("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getMethod(),
                Method.OPTIONS);
        assertEquals(RFC6570RequestBuilder.options("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").getUrlTemplate(),
                "http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}");
    }

    @Test
    public void testHead() {
        assertEquals(RFC6570RequestBuilder.head("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").build().getMethod(),
                Method.HEAD);
        assertEquals(RFC6570RequestBuilder.head("http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}").getUrlTemplate(),
                "http://api-cloud-01.qa:8080/commerce-hyper-api/{?superParam}");
    }



    @Test
    public void testEquals(){

        String fakeUrl = UUID.randomUUID().toString();

        Request request1 = new RFC6570RequestBuilder()
                .setUrlTemplate(fakeUrl)
                .build();

        Request request2 = new RFC6570RequestBuilder()
                .setUrlTemplate(fakeUrl)
                .build();

        assertEquals(request1, request2);



        request2 = new RFC6570RequestBuilder()
                .setUrlTemplate(fakeUrl + "1")
                .build();

        assertFalse(request1.equals(request2));


        //TODO: a whole bunch more of these conditions!
    }



}
