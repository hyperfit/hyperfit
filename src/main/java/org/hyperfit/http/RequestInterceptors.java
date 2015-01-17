package org.hyperfit.http;

import org.hyperfit.utils.DeepCloneable;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Groups interceptors and applies them
 *
 * @author Carlos Perez
 */
@ToString
@EqualsAndHashCode
public class RequestInterceptors implements DeepCloneable {

    private static final Logger LOG = LoggerFactory.getLogger(RequestInterceptors.class);

    //A set is used so the same interceptor is not added more than once
    Set<RequestInterceptor> requestInterceptorSet = new HashSet<RequestInterceptor>();

    //adds a request interceptor.
    public RequestInterceptors add(RequestInterceptor requestInterceptor) {
        if (requestInterceptor != null) {
            this.requestInterceptorSet.add(requestInterceptor);
        }
        return this;
    }

    //removes the interceptors in the array
    public RequestInterceptors remove(RequestInterceptor... requestInterceptor) {
        this.requestInterceptorSet.remove(requestInterceptor);
        return this;
    }

    //deep cloning
    public RequestInterceptors deepClone() {
        RequestInterceptors requestInterceptors = new RequestInterceptors();
        requestInterceptors.requestInterceptorSet.addAll(requestInterceptorSet);
        return requestInterceptors;
    }

    public RequestInterceptors clear() {
        this.requestInterceptorSet.clear();
        return this;
    }

    //intercept the request applying all the interceptors
    public RequestInterceptors intercept(Request.RequestBuilder requestBuilder) {
        for (RequestInterceptor requestInterceptor : requestInterceptorSet) {

            requestInterceptor.intercept(requestBuilder);
            LOG.debug("Request intercepted. Url: {}", requestBuilder.getUrlTemplate());
        }
        return this;
    }
}