package org.hyperfit.net;


import java.util.*;

import lombok.ToString;
import org.hyperfit.utils.Preconditions;
import org.hyperfit.utils.StringUtils;

/**
 * Represents a response message received by the hyper client from a service
 */
@ToString
public class Response {

    private final int code;
    private final Collection<Map.Entry<String, String>> headers;
    private final String body;
    private final String contentType;
    private final Request request;

    private Response(ResponseBuilder builder) {
        this.body = builder.body;
        this.code = builder.code;
        this.headers = Collections.unmodifiableCollection(builder.headers);
        this.contentType = builder.contentType;
        this.request = Preconditions.checkNotNull(builder.request, "request can not be null");
    }

    public int getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }

    public Collection<Map.Entry<String, String>> getHeaders() {
        return headers;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getHeader(String key) {
        for(Map.Entry<String,String> header : this.getHeaders()){
            if(header.getKey().equalsIgnoreCase(key)){
                return header.getValue();
            }
        }
        return null;
    }

    public Request getRequest() {
        return request;
    }

    public boolean isOK() {
        return (this.code > 199) && (this.code < 300);
    }

    public static ResponseBuilder builder() {
        return new ResponseBuilder();
    }

    /**
     * Inner class, Builder class of HyperResponse
    */
    public static class ResponseBuilder {

        private int code;
        private String body;
        private List<Map.Entry<String, String>> headers = new ArrayList<Map.Entry<String, String>>(5);
        private String contentType;
        private Request request;

        public ResponseBuilder() {
        }

        public ResponseBuilder addBody(String body) {
            this.body = body;
            return this;
        }

        public ResponseBuilder addCode(int code) {
            this.code = code;
            return this;
        }

        public ResponseBuilder addHeader(String name, String value) {
            if (StringUtils.isEmpty(name)) {
                throw new IllegalArgumentException("name cannot be empty");
            }

            if (value != null) {
                this.headers.add(new HashMap.SimpleEntry<String, String>(name, value));
            }

            return this;
        }

        public ResponseBuilder addContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public ResponseBuilder addRequest(Request req){
            this.request = req;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }
}
