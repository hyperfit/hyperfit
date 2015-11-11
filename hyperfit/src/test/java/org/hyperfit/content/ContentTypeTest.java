package org.hyperfit.content;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

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

    Random r = new Random();

    @Test
    public void testWithQ(){
        String type = UUID.randomUUID().toString();
        String subtype = UUID.randomUUID().toString();

        String param = "p1";
        String paramVal = UUID.randomUUID().toString();


        Map<String,String> params = new HashMap<String,String>();
        params.put(param, paramVal);

        Double q = r.nextDouble();


        ContentType startingContent = new ContentType(type, subtype, params, q);

        Double newQ = r.nextDouble();

        assertNotEquals(q, newQ);

        ContentType newContent = startingContent.withQ(newQ);

        assertEquals(startingContent.toString(false), newContent.toString(false));
        assertEquals(newQ, newContent.getQualifier(), 0);



    }


}
