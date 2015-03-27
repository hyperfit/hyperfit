package org.hyperfit.errorhandler;


import org.hyperfit.HyperfitProcessor;
import org.hyperfit.content.ContentRegistry;
import org.hyperfit.net.Response;
import org.hyperfit.resource.HyperResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hyperfit.exception.ResponseException;


/**
 * <p>Default implementation of HyperErrorHandler</p>
 * Throws a few general ResponseException types based upon the error code.
 * @see ErrorHandler
 */
public class DefaultErrorHandler implements ErrorHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultErrorHandler.class);


    public HyperResource unhandledContentType(HyperfitProcessor processor, Response response, ContentRegistry contentRegistry, Class<?> expectedResourceInterface) {
        throw new ResponseException(
            String.format("Response from [%s] with code [%s] has unsupported content type [%s]", response.getRequest().getUrl(), response.getCode(), response.getContentType()),
            response
        );
    }

    public HyperResource contentParseError(HyperfitProcessor processor, Response response, ContentRegistry contentRegistry, Class<?> expectedResourceInterface, Exception parseException) {
        throw new ResponseException(
            parseException,
            String.format("Response from [%s] with code [%s] could not be parsed into a hyper resource of content type [%s] because [%s]", response.getRequest().getUrl(), response.getCode(), response.getContentType(), parseException.getMessage()),
            response
        );
    }

    public HyperResource notOKResponse(HyperfitProcessor processor, Response response, ContentRegistry contentRegistry, Class<?> expectedResourceInterface, HyperResource parsedResource) {
        throw new ResponseException(
            String.format("Response from [%s] had not OK status code of [%s]", response.getRequest().getUrl(), response.getCode()),
            response
        );
    }
}
