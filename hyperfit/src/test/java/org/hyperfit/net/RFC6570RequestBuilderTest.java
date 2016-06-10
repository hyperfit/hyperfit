package org.hyperfit.net;


import com.damnhandy.uri.template.VarExploder;
import com.damnhandy.uri.template.VariableExpansionException;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.junit.Test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.LinkedHashMap;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;
import static test.TestUtils.*;


public class RFC6570RequestBuilderTest {



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


    class Selections extends ArrayList<String> implements VarExploder {

        //This is quite a hack here.  Damn handy supports arrays, but only with java.beans.Introspector
        //which it really doesn't need for an array of string, but anyways android doesn't have java.beans.Introspector
        //so it complains so we need to have our own VarExploder
        //But there's no way to do arrays AND have a custom var exploder
        //so you have to have something that is both
        //see https://github.com/damnhandy/Handy-URI-Templates/issues/26
        public Selections(String... selections){
            this.addAll(Arrays.asList(selections));
        }

        public Map<String, Object> getNameValuePairs() throws VariableExpansionException {
            return null;
        }

        public Collection<Object> getValues() throws VariableExpansionException {
            return null;
        }
    }

    @Test
    public void testGetUrlExpandWildCardRepeatingExploder() {

        //we'll use a more complicated template with 2 params to testst
        String xValue = uniqueString();

        VarExploder exploder = new Selections("1","2");

        String actual = new RFC6570RequestBuilder()
            .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?x,param*}")
            .setParam("param", exploder)
            .setParam("x", xValue)
            .build()
            .getUrl();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/?x=" + xValue + "&param=1&param=2", actual);
    }



    @Test
    public void testGetUrlExpandWildCardMap() {
        //we'll use a more complicated template with 2 params to testst
        String xValue = uniqueString();


        LinkedHashMap<String,String> val = new LinkedHashMap<String, String>();
        val.put("x", "1"); //this x is different than x in template
        val.put("y", "cat");

        String actual = new RFC6570RequestBuilder()
            .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?x,param*}")
            .setParam("param", val)
            .setParam("x", xValue)
            .build()
            .getUrl();

        assertEquals("http://api-cloud-01.qa:8080/commerce-hyper-api/?x=" + xValue + "&x=1&y=cat", actual);
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

        RFC6570RequestBuilder request = new RFC6570RequestBuilder().
        setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/{?param}").
        setParam("param", null);

        assertEquals(0, (request.getParams().keySet().size()));
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
        String header1Name = uniqueString();
        String header1Value = uniqueString();

        String header2Name = uniqueString();
        String header2Value = uniqueString();

        Request request = new RFC6570RequestBuilder()
            .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/")
            .addHeader(header1Name, header1Value)
            .addHeader(header2Name, header2Value)
            .build();

        assertEquals(header1Value, request.getHeader(header1Name));
    }

    @Test
    public void testGetHeaderNullValue() {
        String header1Name = uniqueString();
        String header1Value = null;

        String header2Name = uniqueString();
        String header2Value = uniqueString();

        Request request = new RFC6570RequestBuilder()
            .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/")
            .addHeader(header1Name, header1Value)
            .addHeader(header2Name, header2Value)
            .build();


        assertNull(request.getHeader(header1Name));

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
        for (Map.Entry<String, Object> entry : request.getParams().entrySet()) {

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
        for (Map.Entry<String, Object> entry : request.getParams().entrySet()) {

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
        String header1Name = uniqueString();
        String header1Value = uniqueString();

        String header2Name = uniqueString();
        String header2Value = uniqueString();

        Request request = new RFC6570RequestBuilder()
            .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/")
            .addHeader(header1Name, header1Value)
            .addHeader(header2Name, header2Value)
            .build();



        ImmutableList<String> keys = FluentIterable.from(request.getHeaders())
        .transform(new Function<Map.Entry<String, String>, String>() {
            public String apply(Map.Entry<String, String> input) {
                return input.getKey();
            }
        })
        .toList();

        assertThat(keys, containsInAnyOrder(header1Name, header2Name));

        ImmutableList<String> values = FluentIterable.from(request.getHeaders())
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
        String header1Name = uniqueString();
        String header1Value = uniqueString();

        String header2Name = uniqueString();
        String header2Value = null;

        Request request = new RFC6570RequestBuilder()
            .setUrlTemplate("http://api-cloud-01.qa:8080/commerce-hyper-api/")
            .addHeader(header1Name, header1Value)
            .addHeader(header2Name, header2Value)
            .build();

        ImmutableList<String> keys = FluentIterable.from(request.getHeaders())
        .transform(new Function<Map.Entry<String, String>, String>() {
            public String apply(Map.Entry<String, String> input) {
                return input.getKey();
            }
        })
        .toList();

        assertThat(keys, contains(header1Name));

        ImmutableList<String> values = FluentIterable.from(request.getHeaders())
        .transform(new Function<Map.Entry<String, String>, String>() {
            public String apply(Map.Entry<String, String> input) {
                return input.getValue();
            }
        })
        .toList();

        assertThat(values, contains(header1Value));
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

        String fakeUrl = uniqueString();

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
