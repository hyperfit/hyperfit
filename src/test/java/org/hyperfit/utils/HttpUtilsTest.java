package org.hyperfit.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HttpUtilsTest{
    
    @Test
    public void testGetContentTypeWithoutCharset_withCharset() {
        assertEquals("application/hal+json", 
                HttpUtils.getContentTypeWithoutCharset("application/hal+json;charset=UTF-8"));
    }
    
    @Test
    public void testGetContentTypeWithoutCharset_withoutCharset() {
        assertEquals("application/hal+json", 
                HttpUtils.getContentTypeWithoutCharset("application/hal+json"));
    }
    
    @Test
    public void testGetContentTypeWithoutCharset_withComma() {
        assertEquals("application/hal+json", 
                HttpUtils.getContentTypeWithoutCharset("application/hal+json;"));
    }  
}
