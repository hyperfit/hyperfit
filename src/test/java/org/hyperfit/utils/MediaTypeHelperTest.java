package org.hyperfit.utils;

import static org.junit.Assert.assertEquals;

import org.hyperfit.mediatype.MediaTypeHelper;
import org.junit.Test;

public class MediaTypeHelperTest {
    
    @Test
    public void testGetContentTypeWithoutCharset_withCharset() {
        assertEquals("application/hal+json", 
                MediaTypeHelper.getContentTypeWithoutCharset("application/hal+json;charset=UTF-8"));
    }
    
    @Test
    public void testGetContentTypeWithoutCharset_withoutCharset() {
        assertEquals("application/hal+json", 
                MediaTypeHelper.getContentTypeWithoutCharset("application/hal+json"));
    }
    
    @Test
    public void testGetContentTypeWithoutCharset_withComma() {
        assertEquals("application/hal+json", 
                MediaTypeHelper.getContentTypeWithoutCharset("application/hal+json;"));
    }  
}
