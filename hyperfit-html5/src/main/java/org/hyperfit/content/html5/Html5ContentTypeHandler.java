package org.hyperfit.content.html5;


import org.hyperfit.content.ContentType;
import org.hyperfit.content.ContentTypeHandler;
import org.hyperfit.net.RequestBuilder;
import org.hyperfit.net.Response;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.html5.Html5Resource;

/**
 * Html5 HAL Content Type Handler
 */
public class Html5ContentTypeHandler implements ContentTypeHandler {

    private static final ContentType contentType = new ContentType("application", "xhtml+xml");

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
     * @return {@link org.hyperfit.resource.html5.Html5Resource}
    */
    public HyperResource parseResponse(Response response) {

        return new Html5Resource(response);
    }

    public boolean canParseResponse() {
        return true;
    }

    public void prepareRequest(RequestBuilder request, Object content) {
        throw new UnsupportedOperationException();
    }

    public boolean canPrepareRequest() {
        return false;
    }


}
