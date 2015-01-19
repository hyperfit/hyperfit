package org.hyperfit.mediatype.hal.json;


import org.hyperfit.errorhandler.ResponseError;
import org.hyperfit.http.Response;
import org.hyperfit.mediatype.MediaTypeHandler;
import org.hyperfit.message.Messages;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.hal.json.HalJsonResource;

/**
 * HAL JSON Hypermedia media type handler
 */
public class HalJsonMediaTypeHandler implements MediaTypeHandler {

    public HalJsonMediaTypeHandler() {
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public String getDefaultHandledMediaType() {
        return "application/hal+json";
    }

    /**
     * @param response response {@link org.hyperfit.http.Response} to be handled
     * @return {@link org.hyperfit.resource.hal.json.HalJsonResource}
    */
    public HyperResource handleHyperResponse(Response response) {

        // Validate required elements
        if (response == null) {
            throw new NullPointerException(Messages.MSG_ERROR_MEDIATYPE_HYPER_RESPONSE_NULL);
        }

        return new HalJsonResource(response);
    }

    public ResponseError parseError(Response response) {
        if (response.getCode() < 200 || response.getCode() >= 300) {
            HyperResource hyperResource = new HalJsonResource(response);

            String errorTitle = null;
            try {
                errorTitle = hyperResource.getPathAs(String.class, "error");
            } catch (Exception e) {
                //Error element was not present in the resource.
            }
            String errorMessage = hyperResource.getPathAs(String.class, "message");

            return new ResponseError(response.getCode(), errorTitle, errorMessage);

        }
        return null;
    }

}
