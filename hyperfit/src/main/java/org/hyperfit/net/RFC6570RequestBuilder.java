package org.hyperfit.net;

import com.damnhandy.uri.template.UriTemplate;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.utils.StringUtils;

import java.util.*;

/**
 * Builder class for {@link org.hyperfit.net.Request} that uses RFC6570 link template rules
 * see https://tools.ietf.org/html/rfc6570
 */
@ToString
@EqualsAndHashCode
public class RFC6570RequestBuilder implements RequestBuilder {

    private String urlTemplate = null;
    private String contentType = null;
    private String content = null;
    private Method method = Method.GET;

    public Map<String, Object> getParams() {
        return Collections.unmodifiableMap(params);
    }

    private Map<String, Object> params = new HashMap<String, Object>();

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    private Map<String, String> headers = new HashMap<String, String>();

    public Set<String> getAcceptedContentTypes() {
        return Collections.unmodifiableSet(acceptedContentTypes);
    }

    private Set<String> acceptedContentTypes = new HashSet<String>();

    public RFC6570RequestBuilder() {
    }

    public RFC6570RequestBuilder setUrlTemplate(String urlTemplate) {
        if (StringUtils.isEmpty(urlTemplate)) {
            throw new IllegalArgumentException("urlTemplate cannot be empty");
        }

        this.urlTemplate = urlTemplate;
        return this;
    }

    public RFC6570RequestBuilder setContentType(String contentType) {
        if (StringUtils.isEmpty(contentType)) {
            throw new IllegalArgumentException("contentType cannot be empty");
        }

        this.contentType = contentType;
        return this;
    }

    public RFC6570RequestBuilder setMethod(Method method) {
        if (method == null) {
            throw new IllegalArgumentException("method cannot be null");
        }

        this.method = method;
        return this;
    }

    public RFC6570RequestBuilder setContent(String content) {
        if (content == null) {
            throw new IllegalArgumentException("contentType cannot be null");
        }

        this.content = content;
        return this;
    }

    public RFC6570RequestBuilder addHeader(String name, String value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("name cannot be empty");
        }

        if (value != null) {
            this.headers.put(name, value);
        }

        return this;
    }

    //TODO: take a ContentType type instead of a string
    public RFC6570RequestBuilder addAcceptedContentType(String contentType) {
        if (StringUtils.isEmpty(contentType)) {
            throw new IllegalArgumentException("contentType cannot be empty");
        }

        this.acceptedContentTypes.add(contentType);

        return this;
    }

    public RFC6570RequestBuilder setParam(String name, Object value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("name cannot be empty");
        }

        if (value != null) {
            this.params.put(name, value);
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

    public Object getParam(String param) {
        return params.get(param);
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public Request build() {
        return new Request(this);
    }

    public String getURL() {


        try {
            UriTemplate uriTemplateBuilder = UriTemplate.fromTemplate(urlTemplate);

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                uriTemplateBuilder.set(entry.getKey(), entry.getValue());
            }

            return uriTemplateBuilder.expand();
        } catch (Exception e) {
            throw new HyperfitException("URL Template [" + urlTemplate + "] cannot be expanded with params", e);
        }
    }

    /**
     * Set method to GET in RequestBuilder along with httpUrlTemplate
     *
     * @param httpUrlTemplate {@link String} Url Template
     * @return {@link} RequestBuilder
     */
    public static RFC6570RequestBuilder get(String httpUrlTemplate) {
        return new RFC6570RequestBuilder().setMethod(Method.GET).setUrlTemplate(httpUrlTemplate);
    }

    /**
     * Set method to POST in RequestBuilder along with httpUrlTemplate
     *
     * @param httpUrlTemplate {@link String} Url Template
     * @return {@link RFC6570RequestBuilder}
     */
    public static RFC6570RequestBuilder post(String httpUrlTemplate) {
        return new RFC6570RequestBuilder().setMethod(Method.POST).setUrlTemplate(httpUrlTemplate);
    }

    /**
     * Set method to PUT in RequestBuilder along with httpUrlTemplate
     *
     * @param httpUrlTemplate {@link String} Url Template
     * @return {@link RFC6570RequestBuilder}
     */
    public static RFC6570RequestBuilder put(String httpUrlTemplate) {
        return new RFC6570RequestBuilder().setMethod(Method.PUT).setUrlTemplate(httpUrlTemplate);
    }

    /**
     * Set method to DELETE in RequestBuilder along with httpUrlTemplate
     *
     * @param httpUrlTemplate {@link String} Url Template
     * @return {@link RFC6570RequestBuilder}
     */
    public static RFC6570RequestBuilder delete(String httpUrlTemplate) {
        return new RFC6570RequestBuilder().setMethod(Method.DELETE).setUrlTemplate(httpUrlTemplate);
    }

    /**
     * Set method to OPTIONS in RequestBuilder along with httpUrlTemplate
     *
     * @param httpUrlTemplate {@link String} Url Template
     * @return {@link RFC6570RequestBuilder}
     */
    public static RFC6570RequestBuilder options(String httpUrlTemplate) {
        return new RFC6570RequestBuilder().setMethod(Method.OPTIONS).setUrlTemplate(httpUrlTemplate);
    }

    /**
     * Set method to HEAD in RequestBuilder along with httpUrlTemplate
     *
     * @param httpUrlTemplate {@link String} Url Template
     * @return {@link RFC6570RequestBuilder}
     */
    public static RFC6570RequestBuilder head(String httpUrlTemplate) {
        return new RFC6570RequestBuilder().setMethod(Method.HEAD).setUrlTemplate(httpUrlTemplate);
    }

}
