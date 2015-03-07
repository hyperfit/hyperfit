package org.hyperfit.net;

import org.hyperfit.exception.HyperfitException;
import org.hyperfit.message.Messages;
import org.hyperfit.utils.StringUtils;
import com.damnhandy.uri.template.UriTemplate;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.*;
import java.util.Map.Entry;


/**
 * Represents a request message sent by the hyper client to a service
 */
@ToString
public class Request {

    private final String url;
    private final String urlTemplate;
    private final String contentType;
    private final String content;
    private final Method method;
    private final Map<String, String> urlParams;
    private final Map<String, String> headers;
    private final Set<String> acceptedContentTypes;

    public String getUrl() {
        return url;
    }

    public String getUrlTemplate() {
        return urlTemplate;
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

    public Object getUrlParam(String name) {
        return urlParams.get(name);
    }

    public Iterator<Entry<String, String>> getUrlParams() {
        return urlParams.entrySet().iterator();
    }

    public Object getHeader(String name) {
        return headers.get(name);
    }

    public Iterator<Entry<String, String>> getHeaders() {
        return headers.entrySet().iterator();
    }

    public Set<String> getAcceptedContentTypes() {
        return acceptedContentTypes;
    }

    /**
     * Create {@link Request} using {@link org.hyperfit.net.Request.RequestBuilder}
     *
     * @param builder
     */
    private Request(RequestBuilder builder) {
        this.urlParams = builder.urlParams;
        this.urlTemplate = builder.urlTemplate;
        this.contentType = builder.contentType;
        this.content = builder.content;
        this.headers = builder.headers;
        this.method = builder.method;
        this.acceptedContentTypes = Collections.unmodifiableSet(builder.acceptedContentTypes);


        try {
            UriTemplate uriTemplateBuilder = UriTemplate.fromTemplate(urlTemplate);

            for (Entry<String, String> entry : urlParams.entrySet()) {
                uriTemplateBuilder.set(entry.getKey(), entry.getValue());
            }

            this.url = uriTemplateBuilder.expand();
        } catch (Exception e) {
            throw new HyperfitException(e, Messages.MSG_ERROR_REQUEST_URL_CANNOT_BE_EXPANDED, urlTemplate, urlParams);
        }
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
        if (!urlParams.equals(that.urlParams)) return false;
        if (!urlTemplate.equals(that.urlTemplate)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (urlTemplate != null ? urlTemplate.hashCode() : 0);
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (urlParams != null ? urlParams.hashCode() : 0);
        result = 31 * result + (headers != null ? headers.hashCode() : 0);
        result = 31 * result + (acceptedContentTypes.hashCode());
        return result;
    }

    /**
     * Set method to GET in RequestBuilder along with httpUrlTemplate
     *
     * @param httpUrlTemplate {@link String} Url Template
     * @return {@link} RequestBuilder
     */
    public static RequestBuilder get(String httpUrlTemplate) {
        return builder().setMethod(Method.GET).setUrlTemplate(httpUrlTemplate);
    }

    /**
     * Set method to POST in RequestBuilder along with httpUrlTemplate
     *
     * @param httpUrlTemplate {@link String} Url Template
     * @return {@link org.hyperfit.net.Request.RequestBuilder}
     */
    public static RequestBuilder post(String httpUrlTemplate) {
        return builder().setMethod(Method.POST).setUrlTemplate(httpUrlTemplate);
    }

    /**
     * Set method to PUT in RequestBuilder along with httpUrlTemplate
     *
     * @param httpUrlTemplate {@link String} Url Template
     * @return {@link org.hyperfit.net.Request.RequestBuilder}
     */
    public static RequestBuilder put(String httpUrlTemplate) {
        return builder().setMethod(Method.PUT).setUrlTemplate(httpUrlTemplate);
    }

    /**
     * Set method to DELETE in RequestBuilder along with httpUrlTemplate
     *
     * @param httpUrlTemplate {@link String} Url Template
     * @return {@link org.hyperfit.net.Request.RequestBuilder}
     */
    public static RequestBuilder delete(String httpUrlTemplate) {
        return builder().setMethod(Method.DELETE).setUrlTemplate(httpUrlTemplate);
    }

    /**
     * Set method to OPTIONS in RequestBuilder along with httpUrlTemplate
     *
     * @param httpUrlTemplate {@link String} Url Template
     * @return {@link org.hyperfit.net.Request.RequestBuilder}
     */
    public static RequestBuilder options(String httpUrlTemplate) {
        return builder().setMethod(Method.OPTIONS).setUrlTemplate(httpUrlTemplate);
    }

    /**
     * Set method to HEAD in RequestBuilder along with httpUrlTemplate
     *
     * @param httpUrlTemplate {@link String} Url Template
     * @return {@link org.hyperfit.net.Request.RequestBuilder}
     */
    public static RequestBuilder head(String httpUrlTemplate) {
        return builder().setMethod(Method.HEAD).setUrlTemplate(httpUrlTemplate);
    }

    /**
     * Set method to TRACE in RequestBuilder along with httpUrlTemplate
     *
     * @param httpUrlTemplate {@link String} Url Template
     * @return {@link org.hyperfit.net.Request.RequestBuilder}
     */
    public static RequestBuilder trace(String httpUrlTemplate) {
        return builder().setMethod(Method.TRACE).setUrlTemplate(httpUrlTemplate);
    }

    /**
     * Set method to CONNECT in RequestBuilder along with httpUrlTemplate
     *
     * @param httpUrlTemplate {@link String} Url Template
     * @return {@link org.hyperfit.net.Request.RequestBuilder}
     */
    public static RequestBuilder connect(String httpUrlTemplate) {
        return builder().setMethod(Method.CONNECT).setUrlTemplate(httpUrlTemplate);
    }

    public static RequestBuilder builder() {
        return new RequestBuilder();
    }



    /**
     * Builder class for {@link Request}
     */
    @ToString
    @EqualsAndHashCode

    public static class RequestBuilder {

        private String urlTemplate = null;
        private String contentType = null;
        private String content = null;
        private Method method = Method.GET;
        private Map<String, String> urlParams = new HashMap<String, String>();
        private Map<String, String> headers = new HashMap<String, String>();
        private Set<String> acceptedContentTypes = new HashSet<String>();

        private RequestBuilder() {
        }

        public RequestBuilder setUrlTemplate(String urlTemplate) {
            if (StringUtils.isEmpty(urlTemplate)) {
                throw new IllegalArgumentException(Messages.MSG_ERROR_REQUEST_URL_EMPTY);
            }

            this.urlTemplate = urlTemplate;
            return this;
        }

        public RequestBuilder setContentType(String contentType) {
            if (StringUtils.isEmpty(contentType)) {
                throw new IllegalArgumentException(Messages.MSG_ERROR_REQUEST_CONTENT_TYPE_EMPTY);
            }

            this.contentType = contentType;
            return this;
        }

        public RequestBuilder setMethod(Method method) {
            if (method == null) {
                throw new NullPointerException(Messages.MSG_ERROR_REQUEST_METHOD_NULL);
            }

            this.method = method;
            return this;
        }

        public RequestBuilder setContent(String content) {
            if (content == null) {
                throw new NullPointerException(Messages.MSG_ERROR_REQUEST_CONTENT_BODY_NULL);
            }

            this.content = content;
            return this;
        }

        public RequestBuilder addHeader(String name, String value) {
            if (StringUtils.isEmpty(name)) {
                throw new IllegalArgumentException(Messages.MSG_ERROR_REQUEST_HEADER_NAME_EMPTY);
            }

            if (value != null) {
                this.headers.put(name, value);
            }

            return this;
        }

        //TODO: take a ContentType type instead of a string
        public RequestBuilder addAcceptedContentType(String name) {
            if (StringUtils.isEmpty(name)) {
                throw new IllegalArgumentException(Messages.MSG_ERROR_REQUEST_ACCEPTED_CONTENT_TYPE_EMPTY);
            }

            this.acceptedContentTypes.add(name);

            return this;
        }

        public RequestBuilder setUrlParam(String name, String value) {
            if (StringUtils.isEmpty(name)) {
                throw new IllegalArgumentException(Messages.MSG_ERROR_REQUEST_URL_PARAM_NAME_EMPTY);
            }

            if (value != null) {
                this.urlParams.put(name, value);
            }

            return this;
        }

        public String getUrlTemplate() {
            return urlTemplate;
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

        public String getUrlParam(String param) {
            return urlParams.get(param);
        }

        public String getHeader(String header) {
            return headers.get(header);
        }

        public Request build() {
            return new Request(this);
        }

    }

}
