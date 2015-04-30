package org.hyperfit.net;

import org.hyperfit.exception.HyperfitException;
import org.hyperfit.message.Messages;
import org.hyperfit.resource.html5.controls.form.JsoupHtmlForm;
import org.hyperfit.utils.StringUtils;

import java.util.*;

public class HTMLFormRequestBuilder implements RequestBuilder {

    private final String url;
    private final static String contentType = "application/x-www-form-urlencoded";
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


    public HTMLFormRequestBuilder(JsoupHtmlForm form){
        url = form.getHref();
        this.setMethod(form.getMethod());
    }

    public HTMLFormRequestBuilder addHeader(String name, String value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Messages.MSG_ERROR_REQUEST_HEADER_NAME_EMPTY);
        }

        if (value != null) {
            this.headers.put(name, value);
        }

        return this;
    }

    //TODO: take a ContentType type instead of a string
    public HTMLFormRequestBuilder addAcceptedContentType(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Messages.MSG_ERROR_REQUEST_ACCEPTED_CONTENT_TYPE_EMPTY);
        }

        this.acceptedContentTypes.add(name);

        return this;
    }

    public HTMLFormRequestBuilder setParam(String name, Object value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Messages.MSG_ERROR_REQUEST_URL_PARAM_NAME_EMPTY);
        }

        if (value != null) {
            this.params.put(name, value);
        }

        return this;
    }

    @Override
    public RequestBuilder setContentType(String contentType) {
        //TODO: should this throw, respect the request, or just do nothing like it is?
        return this;
    }

    @Override
    public RequestBuilder setMethod(Method method) {
        this.method = method;
        return this;
    }

    @Override
    public RequestBuilder setContent(String content) {
        throw new HyperfitException("HTMLFormRequestBuilder cannot have it's content set directly");
    }


    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
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


    @Override
    public String getURL() {
        return url;
    }

}
