package org.hyperfit.content.form;

import org.hyperfit.content.ContentType;
import org.hyperfit.net.RFC6570RequestBuilder;
import org.hyperfit.net.Request;
import org.hyperfit.net.RequestBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;


public class FormUrlEncodedContentTypeHandlerTest {



    FormURLEncodedContentTypeHandler handler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        handler = new FormURLEncodedContentTypeHandler();
    }

    @Test
    public void testGetDefaultHandledMediaType() {
        assertEquals(new ContentType("application", "x-www-form-urlencoded"), handler.getDefaultContentType());
    }


    static final class RequestData {

        private String stringVal = "StringVal";
        private String stringWithChars = "!@#$%^&*()";
        private String stringUrl = "http://host/path?xxx=yy&bbb";

        private Double DoubleValue = 66.77d;
        private double doubleValue = 55.77d;

        private Integer IntegerValue = 66;
        private int intValue = 55;

        private Boolean BooleanValue = Boolean.FALSE;
        private boolean booleanValue = true;


    }

    @Test
    public void testEncodeRequest(){
        RequestData content = new RequestData();
        RequestBuilder builder = new RFC6570RequestBuilder();

        handler.prepareRequest(builder, content);

        assertEquals(handler.getDefaultContentType().toString(false), builder.getContentType());


        assertEquals("stringVal=StringVal&stringWithChars=%21%40%23%24%25%5E%26*%28%29&stringUrl=http%3A%2F%2Fhost%2Fpath%3Fxxx%3Dyy%26bbb&DoubleValue=66.77&doubleValue=55.77&IntegerValue=66&intValue=55&BooleanValue=false&booleanValue=true", builder.getContent());
    }




}
