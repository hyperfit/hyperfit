package org.hyperfit.http;

import org.hyperfit.message.Messages;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import lombok.ToString;
import org.hyperfit.utils.StringUtils;

/**
 * Represents a response message received by the hyper client from a service
 */
@ToString
public class Response {

    private final int code;
    private final Map<String, String> headers;
    private final String body;

    private Response(ResponseBuilder builder) {
        this.body = builder.body;
        this.code = builder.code;
        this.headers = builder.headers;
    }

    public int getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }

    public Iterator<Map.Entry<String, String>> getHeaders() {
        return headers.entrySet().iterator();
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public static ResponseBuilder builder() {
        return new ResponseBuilder();

    }   
    
    public boolean isOK() {
        return (this.code == 200);
    }

    /**
     * Inner class, Builder class of HyperResponse
    */
    public static class ResponseBuilder {

        private int code;
        private String body;
        private Map<String, String> headers = new HashMap<String, String>();

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
                throw new IllegalArgumentException(Messages.MSG_ERROR_RESPONSE_HEADER_NAME_EMPTY);
            }

            if (value != null) {
                this.headers.put(name, value);
            }

            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }
}
