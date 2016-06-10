package org.hyperfit.net;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.utils.StringUtils;

import java.util.*;
import java.util.Map.Entry;


/**
 * Represents a request message sent by the hyper client to a service
 */
@ToString
@EqualsAndHashCode
public class Request {

    private final String url;
    private final String contentType;
    private final String content;
    private final Method method;
    private final Map<String, String> headers;
    private final Set<String> acceptedContentTypes;

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }

    public Method getMethod() {
        return method;
    }

    public Object getHeader(String name) {
        return headers.get(name);
    }

    public Iterable<Entry<String, String>> getHeaders() {
        return Collections.unmodifiableSet(headers.entrySet());
    }

    public Set<String> getAcceptedContentTypes() {
        return acceptedContentTypes;
    }

    /**
     * Create {@link Request} using {@link RequestBuilder}'s like {@link RFC6570RequestBuilder}
     *
     * @param builder
     */
    protected Request(RequestBuilder builder) {
        if(StringUtils.isEmpty(builder.getURL())){
            throw new IllegalArgumentException("url can not be null or empty");
        }

        this.url = builder.getURL();

        this.contentType = builder.getContentType();
        this.content = builder.getContent();
        this.headers = builder.getHeaders();
        this.method = builder.getMethod();
        this.acceptedContentTypes = Collections.unmodifiableSet(builder.getAcceptedContentTypes());

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request that = (Request) o;

        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (contentType != null ? !contentType.equals(that.contentType) : that.contentType != null) return false;
        if (!headers.equals(that.headers)) return false;
        if (method != that.method) return false;
        if (!url.equals(that.url)) return false;

        return true;
    }

}
