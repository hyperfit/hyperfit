package org.hyperfit.content.hal.json;


import org.hyperfit.content.ContentType;
import org.hyperfit.net.Request;
import org.hyperfit.net.Response;
import org.hyperfit.content.ContentTypeHandler;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.hal.json.HalJsonResource;

/**
 * HAL JSON Hypermedia media type handler
 */
public class HalJsonContentTypeHandler implements ContentTypeHandler {

    private static final ContentType contentType = new ContentType("application", "hal+json");

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public ContentType getDefaultContentType() {
        return contentType;
    }

    /**
     * @param response response {@link org.hyperfit.net.Response} to be handled
     * @return {@link org.hyperfit.resource.hal.json.HalJsonResource}
    */
    public HyperResource parseResponse(Response response) {

        return new HalJsonResource(response);
    }

    public void encodeRequest(Request.RequestBuilder request, Object resource) {
        throw new UnsupportedOperationException();
    }


}
