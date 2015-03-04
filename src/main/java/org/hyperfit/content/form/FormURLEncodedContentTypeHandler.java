package org.hyperfit.content.form;

import org.hyperfit.content.ContentType;
import org.hyperfit.content.ContentTypeHandler;
import org.hyperfit.net.Request;
import org.hyperfit.net.Response;
import org.hyperfit.resource.HyperResource;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;

public class FormURLEncodedContentTypeHandler implements ContentTypeHandler {

    private static final String encoding = "UTF-8";
    private static final ContentType type = new ContentType("application", "x-www-form-urlencoded");
    public ContentType getDefaultContentType() {
        return type;
    }

    public HyperResource parseResponse(Response response) {
        throw new UnsupportedOperationException();
    }

    public void encodeRequest(Request.RequestBuilder request, Object resource) {
        request.setContentType(type.toString(false));

        StringBuilder body = new StringBuilder();

        Field[] fields = resource.getClass().getDeclaredFields();
        for(int i = 0; i < fields.length; i++){
            Field f = fields[i];
            f.setAccessible(true);
            try {
                body.append(encode(f.getName())).append("=").append(encode(f.get(resource).toString()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("error generating request body for param " + f.getName(), e);
            }
            if(i+1 < fields.length){
                body.append("&");
            }
        }

        request.setContent(body.toString());


    }

    private static String encode(String s){
        try {
            return URLEncoder.encode(s, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("error generating request body", e);
        }
    }
}
