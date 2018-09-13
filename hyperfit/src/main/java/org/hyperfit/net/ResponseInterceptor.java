package org.hyperfit.net;

/**
 * Intercept response.
 * @deprecated create a custom step in the Response to HyperResource pipeline to intercept responses
 */
@Deprecated
public interface ResponseInterceptor {

    void intercept(Response response);

}
