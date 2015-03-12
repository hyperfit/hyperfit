package org.hyperfit.net;

/**
 * Intercept every request before it is executed in order to add additional data.
 */
public interface RequestInterceptor {

    void intercept(RequestBuilder requestBuilder);
}
