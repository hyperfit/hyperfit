package org.hyperfit.net;

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
     * Sets the content types that are listed as acceptable for any request
     * @param contentTypes A {@link Set} of {@link String} represents contentTypes
     */
    HyperClient setAcceptedContentTypes(Set<String> contentTypes);



    /**
     * Set the CookieHandler to be used for requests made by and responses handled by this client
     * @param handler
     */
    HyperClient setCookieHandler(CookieHandler handler);

    /**
     * Returns the URL schemes this HyperClient implementation supports by default
     * @return
     */
    String[] getSchemes();
}
