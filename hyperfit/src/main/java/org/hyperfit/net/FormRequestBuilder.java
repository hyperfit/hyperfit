package org.hyperfit.net;

import org.hyperfit.exception.HyperfitException;
import org.hyperfit.message.Messages;
import org.hyperfit.resource.controls.form.CheckboxField;
import org.hyperfit.resource.controls.form.Field;
import org.hyperfit.resource.controls.form.Form;
import org.hyperfit.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class FormRequestBuilder implements RequestBuilder {

    private final String url;
    private final static String contentType = "application/x-www-form-urlencoded";
    private String content = null;
    private Method method = Method.GET;
    private final Form form;


    public Map<String, Object> getParams() {
        return Collections.unmodifiableMap(params);
    }

    //I use a linked hash map because of all the server frameworks that will use the first param instance so i maintain order
    //TODO: support multiple values of a param...
    private Map<String, Object> params = new LinkedHashMap<String, Object>();

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    private Map<String, String> headers = new HashMap<String, String>();

    public Set<String> getAcceptedContentTypes() {
        return Collections.unmodifiableSet(acceptedContentTypes);
    }

    private Set<String> acceptedContentTypes = new HashSet<String>();


    public FormRequestBuilder(Form form){
        url = form.getHref();
        this.setMethod(form.getMethod());
        this.form = form;
    }

    public FormRequestBuilder addHeader(String name, String value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Messages.MSG_ERROR_REQUEST_HEADER_NAME_EMPTY);
        }

        if (value != null) {
            this.headers.put(name, value);
        }

        return this;
    }

    //TODO: take a ContentType type instead of a string
    public FormRequestBuilder addAcceptedContentType(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Messages.MSG_ERROR_REQUEST_ACCEPTED_CONTENT_TYPE_EMPTY);
        }

        this.acceptedContentTypes.add(name);

        return this;
    }

    public FormRequestBuilder setParam(String name, Object value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException(Messages.MSG_ERROR_REQUEST_URL_PARAM_NAME_EMPTY);
        }


        Field field = form.getField(name);
        //TODO: call field.validate or something i dunno
        if(field instanceof CheckboxField){
            //Checkboxes don't let you set a value, just opt in to sending the value
            if(value == CheckboxField.CheckState.CHECKED){
                value = field.getValue();
            } else {
                value = null;
            }
        }

        if (value != null) {
            this.params.put(name, value);
        }

        return this;
    }

    public RequestBuilder setContentType(String contentType) {
        //TODO: should this throw, respect the request, or just do nothing like it is?
        return this;
    }

    public RequestBuilder setMethod(Method method) {
        this.method = method;
        return this;
    }

    public RequestBuilder setContent(String content) {
        throw new HyperfitException("HTMLFormRequestBuilder cannot have it's content set directly");
    }


    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        if(params.isEmpty()){
            return "";
        }

        StringBuilder body = new StringBuilder();
        for(Map.Entry<String,Object> entry : params.entrySet()){
            body.append(encode(entry.getKey()))
            .append("=")
            .append(encode(entry.getValue().toString()))
            .append("&");

        }

        return body.deleteCharAt(body.length() -1).toString();
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
        return url;
    }

    private static final String encoding = "UTF-8";
    private static String encode(String s){
        try {
            return URLEncoder.encode(s, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("error generating request body", e);
        }
    }

}
