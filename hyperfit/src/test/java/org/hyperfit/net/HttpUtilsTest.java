package org.hyperfit.net;

import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;

/**
 * Utility class for working with http requests and responses
 */
public class HttpUtilsTest {


    @Test
    public void testEmpty(){
        //Is this good? maybe it should throw an exception?
        //or return *.* ?
        String result = HttpUtils.buildAcceptHeaderValue();
        assertEquals("", result);


        result = HttpUtils.buildAcceptHeaderValue(
            new HashSet<String>()
        );
        assertEquals("", result);


        result = HttpUtils.buildAcceptHeaderValue(
            new HashSet<String>(),
            new HashSet<String>()
        );
        assertEquals("", result);

    }



    @Test
    public void testOrderPreserved(){
        LinkedHashSet<String> setA = new LinkedHashSet<String>();
        setA.add("a1");
        setA.add("a2");
        setA.add("a3");

        LinkedHashSet<String> setB = new LinkedHashSet<String>();
        setB.add("b1");
        setB.add("b2");
        setB.add("b3");

        String result = HttpUtils.buildAcceptHeaderValue(
            setA,
            setB
        );
        assertEquals("a1,a2,a3,b1,b2,b3", result);


        result = HttpUtils.buildAcceptHeaderValue(
            setB,
            setA
        );
        assertEquals("b1,b2,b3,a1,a2,a3", result);

    }
}
