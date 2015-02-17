package org.hyperfit.errorhandler;


import org.hyperfit.exception.*;
import org.hyperfit.mediatype.MediaTypeHandler;
import org.hyperfit.net.Request;
import org.hyperfit.net.Response;
import org.hyperfit.resource.HyperResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hyperfit.exception.ResponseException;

import java.util.Map;

/**
 * <p>Default implementation of HyperErrorHandler</p>
 * Throws a few general ResponseException types based upon the error code.
 * @see ErrorHandler
 */
public class DefaultErrorHandler implements ErrorHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultErrorHandler.class);


    public HyperResource unhandledContentType(Request request, Response response, Map<String, MediaTypeHandler> contentTypeHandlers, Class<?> expectedResourceInterface) {
        throw new ResponseException(
            String.format("Response from [%s] has unsupported content type [%s]", request.getUrl(), response.getContentType()),
            request,
            response
        );
    }

    public HyperResource contentParseError(Request request, Response response, Map<String, MediaTypeHandler> contentTypeHandlers, Class<?> expectedResourceInterface, Exception parseException) {
        throw new ResponseException(
            parseException,
            String.format("Response from [%s] could not be parsed into a hyper resource of content type [%s] because [%s]", request.getUrl(), response.getContentType(), parseException.getMessage()),
            request,
            response
        );
    }

    public HyperResource notOKResponse(Request request, Response response, Map<String, MediaTypeHandler> contentTypeHandlers, Class<?> expectedResourceInterface, HyperResource parsedResource) {
        throw new ResponseException(
            String.format("Response from [%s] had not OK status code of [%s]", request.getUrl(), response.getCode()),
            request,
            response
        );
    }
}
