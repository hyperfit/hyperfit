package org.hyperfit.mediatype;

import org.hyperfit.errorhandler.ResponseError;
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
 *In this version of client, only HAL type is handled
 */
public interface MediaTypeHandler {

    /**
     *
     * @return {@link String} media type the handler will handle
     */
    String getDefaultHandledMediaType();

    /**
     *
     * @param response response {@link org.hyperfit.net.Response} to be handled
     * @return {@link org.hyperfit.resource.HyperResource}
     */
    HyperResource handleHyperResponse(Response response);
    
    ResponseError parseError(Response response);
}
