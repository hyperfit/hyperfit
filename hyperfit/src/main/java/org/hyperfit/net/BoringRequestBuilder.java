package org.hyperfit.net;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.message.Messages;
import org.hyperfit.utils.StringUtils;

import java.util.*;

/**
 * Builder class for {@link Request} that is pretty boring, doesn't handle params or anything
 */
@ToString
@EqualsAndHashCode
public class BoringRequestBuilder implements RequestBuilder {

    private String url = null;
    private String contentType = null;
    private String content = null;
    private Method method = Method.GET;

    public Map<String, Object> getParams() {
        return Collections.emptyMap();
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    private Map<String, String> headers = new HashMap<String, String>();

    public Set<String> getAcceptedContentTypes() {
        return Collections.unmodifiableSet(acceptedContentTypes);
    }

    private Set<String> acceptedContentTypes = new HashSet<String>();


    public BoringRequestBuilder() {

    }

    private BoringRequestBuilder(String url) {
        setUrl(url);
    }

    public BoringRequestBuilder setUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException(Messages.MSG_ERROR_REQUEST_URL_EMPTY);
        }

        this.url = url;
        return this;
    }

    public BoringRequestBuilder setContentType(String contentType) {
        if (StringUtils.isEmpty(contentType)) {
            throw new IllegalArgumentException(Messages.MSG_ERROR_REQUEST_CONTENT_TYPE_EMPTY);
        }

        this.contentType = contentType;
        return this;
    }


    public BoringRequestBuilder setMethod(Method method) {
        if (method == null) {
            throw new NullPointerException(Messages.MSG_ERROR_REQUEST_METHOD_NULL);
        }

        this.method = method;
        return this;
    }

    public BoringRequestBuilder setContent(String content) {
        if (content == null) {
            throw new NullPointerException(Messages.MSG_ERROR_REQUEST_CONTENT_BODY_NULL);
        }

        this.content = content;
        return this;
    }

    public BoringRequestBuilder addHeader(String name, String value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Messages.MSG_ERROR_REQUEST_HEADER_NAME_EMPTY);
        }

        if (value != null) {
            this.headers.put(name, value);
        }

        return this;
    }


    public BoringRequestBuilder addAcceptedContentType(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Messages.MSG_ERROR_REQUEST_ACCEPTED_CONTENT_TYPE_EMPTY);
        }

        this.acceptedContentTypes.add(name);

        return this;
    }

    public BoringRequestBuilder setParam(String name, Object value) {
        throw new RuntimeException("Boring Request Builder don't know no params");
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }

    public Method getMethod() {
        return method;
    }

    public Object getParam(String param) {
        return null;
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public Request build() {
        return new Request(this);
    }

    public String getURL() {
        return url;
    }

    /**
     * Set method to GET in RequestBuilder along with httpUrlTemplate
     *
     * @param url {@link String} Url
     * @return {@link} RequestBuilder
     */
    public static BoringRequestBuilder get(String url) {
        return new BoringRequestBuilder(url).setMethod(Method.GET);
    }

    /**
     * Set method to POST in RequestBuilder along with httpUrlTemplate
     *
     * @param url {@link String} Url
     * @return {@link org.hyperfit.net.BoringRequestBuilder}
     */
    public static BoringRequestBuilder post(String url) {
        return new BoringRequestBuilder(url).setMethod(Method.POST);
    }

    /**
     * Set method to PUT in RequestBuilder along with httpUrlTemplate
     *
     * @param url {@link String} Url
     * @return {@link org.hyperfit.net.BoringRequestBuilder}
     */
    public static BoringRequestBuilder put(String url) {
        return new BoringRequestBuilder(url).setMethod(Method.PUT);
    }

    /**
     * Set method to DELETE in RequestBuilder along with httpUrlTemplate
     *
     * @param url {@link String} Url
     * @return {@link org.hyperfit.net.BoringRequestBuilder}
     */
    public static BoringRequestBuilder delete(String url) {
        return new BoringRequestBuilder(url).setMethod(Method.DELETE);
    }

    /**
     * Set method to OPTIONS in RequestBuilder along with httpUrlTemplate
     *
     * @param url {@link String} Url
     * @return {@link org.hyperfit.net.BoringRequestBuilder}
     */
    public static BoringRequestBuilder options(String url) {
        return new BoringRequestBuilder(url).setMethod(Method.OPTIONS);
    }

    /**
     * Set method to HEAD in RequestBuilder along with httpUrlTemplate
     *
     * @param url {@link String} Url
     * @return {@link org.hyperfit.net.BoringRequestBuilder}
     */
    public static BoringRequestBuilder head(String url) {
        return new BoringRequestBuilder(url).setMethod(Method.HEAD);
    }

}
