package org.hyperfit.content;

import org.hyperfit.net.RequestBuilder;
import org.hyperfit.net.Response;
import org.hyperfit.resource.HyperResource;

/**
 *Define Handler for hypermedia response. Existing common hypermedia type includes:
 * <ul>
 *     <li>XHTM</li>
 *     <li>Collection+JSON</li>
 *     <li>HAL</li>
 *     <li>Siren</li>
 * </ul>
 */
public interface ContentTypeHandler {

    /**
     *
     * @return {@link String} media type the handler will handle
     */
    ContentType getDefaultContentType();

    /**
     *
     * @param response response {@link org.hyperfit.net.Response} to be handled
     * @return {@link org.hyperfit.resource.HyperResource}
     */
    HyperResource parseResponse(Response response);

    boolean canParseResponse();


    void prepareRequest(RequestBuilder request, Object content);

    boolean canPrepareRequest();

}
