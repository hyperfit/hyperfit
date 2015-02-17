package org.hyperfit.mediatype.hal.json;


import org.hyperfit.net.Response;
import org.hyperfit.mediatype.MediaTypeHandler;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.hal.json.HalJsonResource;

/**
 * HAL JSON Hypermedia media type handler
 */
public class HalJsonMediaTypeHandler implements MediaTypeHandler {

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public String getDefaultHandledMediaType() {
        return "application/hal+json";
    }

    /**
     * @param response response {@link org.hyperfit.net.Response} to be handled
     * @return {@link org.hyperfit.resource.hal.json.HalJsonResource}
    */
    public HyperResource parseHyperResponse(Response response) {

        return new HalJsonResource(response);
    }


}
