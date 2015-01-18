package org.hyperfit.mediatype;

import com.squareup.okhttp.MediaType;
import org.hyperfit.utils.StringUtils;

public class MediaTypeHelper {

    private MediaTypeHelper() { }
    
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
