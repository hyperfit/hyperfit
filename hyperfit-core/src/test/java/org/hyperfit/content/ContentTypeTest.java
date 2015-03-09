package org.hyperfit.content;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ContentTypeTest {

    @Test
    public void testParse(){
        ContentType actual = ContentType.parse("main/sub");
        ContentType expected = new ContentType("main", "sub");

        assertEquals(expected, actual);


    }


    @Test
    public void testParseWithQ(){
        ContentType actual = ContentType.parse("main/sub;q=.7123");
        ContentType expected = new ContentType("main", "sub", null, .7123d);

        assertEquals(expected, actual);
    }


    @Test
    public void testParseWithQAndCharSet(){
        ContentType actual = ContentType.parse("main/sub;charset=UTF-8;q=.7123");
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("charset", "UTF-8");

        ContentType expected = new ContentType("main", "sub", params, .7123d);

        assertEquals(expected, actual);
    }


    @Test
    public void testCompatibleWith(){
        ContentType a = ContentType.parse("main/sub");
        ContentType b = ContentType.parse("main/sub");

        assertTrue(a.compatibleWith(b));


        ContentType c = ContentType.parse("main/dog");
        assertFalse(a.compatibleWith(c));


        c = ContentType.parse("stuff/sub");
        assertFalse(a.compatibleWith(c));

    }


}
