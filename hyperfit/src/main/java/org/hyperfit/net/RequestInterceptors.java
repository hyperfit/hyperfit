package org.hyperfit.net;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Groups interceptors and applies them
 */
@ToString
@EqualsAndHashCode
public class RequestInterceptors {

    private static final Logger LOG = LoggerFactory.getLogger(RequestInterceptors.class);

    //A set is used so the same interceptor is not added more than once
    private final Set<RequestInterceptor> interceptors = new HashSet<RequestInterceptor>();

    /**
     * Creates a new instance containing all of the interceptors of the passed in instance.
     * This is roughly a shallow clone
     * @param requestInterceptors
     */
    public RequestInterceptors(RequestInterceptors requestInterceptors) {
        this.interceptors.addAll(requestInterceptors.interceptors);
    }

    public RequestInterceptors() {

    }

    //adds a request interceptor.
    public RequestInterceptors add(RequestInterceptor requestInterceptor) {
        if (requestInterceptor != null) {
            this.interceptors.add(requestInterceptor);
        }
        return this;
    }

    //removes the interceptors in the array
    public RequestInterceptors remove(RequestInterceptor... requestInterceptor) {
        this.interceptors.remove(requestInterceptor);
        return this;
    }


    public RequestInterceptors remove(Class<? extends RequestInterceptor> typeToRemove) {
        for (java.util.Iterator<RequestInterceptor> i = interceptors.iterator(); i.hasNext();) {

            RequestInterceptor element = i.next();
            if (typeToRemove.isInstance(element)) {
                i.remove();
            }
        }
        return this;
    }

    public RequestInterceptors clear() {
        this.interceptors.clear();
        return this;
    }

    //intercept the request applying all the interceptors
    public RequestInterceptors intercept(RequestBuilder requestBuilder) {
        LOG.trace("Running {} interceptors for request: {}", interceptors.size(), requestBuilder);

        for (RequestInterceptor requestInterceptor : interceptors) {

            requestInterceptor.intercept(requestBuilder);

        }
        return this;
    }
}