package org.hyperfit.http;

import java.net.CookieHandler;
import java.util.Set;

/**
 *  The contract required by an implementation to make requests and receive responses
 */
public interface HyperClient {

    /**
     * Return Response for given Request
     * @param request {@link Request}
     * @return {@link Response}
     */
    Response execute(Request request);

    /**
     * Set ordered mediatypes present in the Accept header of requests made by this client
     * @param mediaTypes A {@link Set} of {@link String} represents mediaTypes
     */
    HyperClient setAcceptedMediaTypes(Set<String> mediaTypes);



    /**
     * Set the CookieHandler to be used for requests made by and responses handled by this client
     * @param handler
     */
    HyperClient setCookieHandler(CookieHandler handler);
}
