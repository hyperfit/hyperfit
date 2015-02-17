package org.hyperfit.errorhandler;

import org.hyperfit.mediatype.MediaTypeHandler;
import org.hyperfit.net.Request;
import org.hyperfit.net.Response;
import org.hyperfit.resource.HyperResource;

import java.util.Map;


/**
 * Defines the cases for which an exception can be handled internally within the Hyperfit execution pipeline.
 * It is suggested that implementations extend the DefaultErrorHandler class
 *
 * Processing a response from the server is done in the following stages
 * Stage 1 - Identify response content type and find an appropriate content type handler.
 * Stage 2 - Parse the response into a basic HyperResource
 * Stage 3 - Verify the response code is a healthy one
 * Stage 4 - Assigning the resource type specific interfaces both claimed by the resource via profiles as well as
 *   the interface expected to be returned by the calling code (usually the return type, but could also be the interface
 *   provided when calling follow() on a link.
 *
 * This interface allows you to interject when issues arise during these stages.  The handler has two options
 * return a hyper resource that can be used or throw an exception derived from HyperfitException
 *
 * The handler should ONLY return a HyperResource if it knows that HyperResource implements the desired interface
 *
 */
public interface ErrorHandler {



    HyperResource unhandledContentType(Request request, Response response, Map<String, MediaTypeHandler> contentTypeHandlers, Class<?> expectedResourceInterface);


    HyperResource contentParseError(Request request, Response response, Map<String, MediaTypeHandler> contentTypeHandlers, Class<?> expectedResourceInterface, Exception parseException);


    HyperResource notOKResponse(Request request, Response response, Map<String, MediaTypeHandler> contentTypeHandlers, Class<?> expectedResourceInterface, HyperResource parsedResource);


    //TODO: handle this situation
    //HyperResource unexpectedResponseType(Request request, Response response, HyperResource parsedResource);

    
}
