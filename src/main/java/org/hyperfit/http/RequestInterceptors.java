package org.hyperfit.http;

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
public class RequestInterceptors {

    private static final Logger LOG = LoggerFactory.getLogger(RequestInterceptors.class);

    //A set is used so the same interceptor is not added more than once
    final Set<RequestInterceptor> requestInterceptorSet = new HashSet<RequestInterceptor>();

    /**
     * Creates a new instance containing all of the interceptors of the passed in instance.
     * This is roughly a shallow clone
     * @param requestInterceptors
     */
    public RequestInterceptors(RequestInterceptors requestInterceptors) {
        this.requestInterceptorSet.addAll(requestInterceptors.requestInterceptorSet);
    }

    public RequestInterceptors() {

    }

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



    public RequestInterceptors clear() {
        this.requestInterceptorSet.clear();
        return this;
    }

    //intercept the request applying all the interceptors
    public RequestInterceptors intercept(Request.RequestBuilder requestBuilder) {
        for (RequestInterceptor requestInterceptor : requestInterceptorSet) {

            requestInterceptor.intercept(requestBuilder);
            LOG.trace("Request intercepted. Url: {}", requestBuilder.getUrlTemplate());
        }
        return this;
    }
}