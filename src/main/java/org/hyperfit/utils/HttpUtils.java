package org.hyperfit.utils;

import com.squareup.okhttp.MediaType;

public class HttpUtils {

    private HttpUtils() { }
    
    public static String getContentTypeWithoutCharset(String contentTypeHeader) {
        String mediaTypeWithoutCharset = null;
        
        MediaType mediaType = MediaType.parse(contentTypeHeader);

        if (mediaType != null) {
            mediaTypeWithoutCharset = mediaType.type();

            if (!StringUtils.isEmpty(mediaType.subtype())) {
                mediaTypeWithoutCharset += "/" + mediaType.subtype();
            }
        }
        return mediaTypeWithoutCharset;
    }    
}
