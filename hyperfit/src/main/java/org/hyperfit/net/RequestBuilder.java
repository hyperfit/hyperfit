package org.hyperfit.net;

import java.util.Map;
import java.util.Set;

public interface RequestBuilder {
    RequestBuilder setContentType(String contentType);

    RequestBuilder setMethod(Method method);

    RequestBuilder setContent(String content);

    RequestBuilder addHeader(String name, String value);

    //TODO: take a ContentType type instead of a string
    RequestBuilder addAcceptedContentType(String contentType);

    RequestBuilder setParam(String name, Object value);

    String getContentType();

    String getContent();

    Method getMethod();

    Object getParam(String param);

    Map<String, Object> getParams();

    String getHeader(String header);

    Map<String, String> getHeaders();

    Set<String> getAcceptedContentTypes();

    String getURL();

    Request build();
}
