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

    public boolean canParseResponse() {
        return false;
    }

    public void prepareRequest(Request.RequestBuilder request, Object content) {
        request.setContentType(type.toString(false));

        StringBuilder body = new StringBuilder();

        Field[] fields = content.getClass().getDeclaredFields();
        for(int i = 0; i < fields.length; i++){
            Field f = fields[i];
            if(f.isSynthetic()){
                //Skip these for jacoco builds see http://www.eclemma.org/jacoco/trunk/doc/faq.html
                //q: My code uses reflection. Why does it fail when I execute it with JaCoCo?
                continue;
            }
            f.setAccessible(true);

            if(i > 0){
                body.append("&");
            }

            try {
                body.append(encode(f.getName())).append("=").append(encode(f.get(content).toString()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("error generating request body for param " + f.getName(), e);
            }

        }

        request.setContent(body.toString());


    }

    public boolean canPrepareRequest() {
        return true;
    }

    private static String encode(String s){
        try {
            return URLEncoder.encode(s, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("error generating request body", e);
        }
    }
}
