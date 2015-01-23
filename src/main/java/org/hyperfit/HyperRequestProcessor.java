package org.hyperfit;


import org.hyperfit.errorhandler.ErrorHandler;
import org.hyperfit.errorhandler.ResponseError;
import org.hyperfit.net.*;
import org.hyperfit.mediatype.MediaTypeHandler;
import org.hyperfit.message.Messages;

import org.hyperfit.methodinfo.ResourceMethodInfoCache;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.registry.ProfileResourceRegistryRetrievalStrategy;
import org.hyperfit.resource.registry.ResourceRegistry;
import org.hyperfit.mediatype.MediaTypeHelper;
import org.hyperfit.utils.ReflectUtils;
import org.hyperfit.utils.TypeInfo;
import org.javatuples.Pair;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Processes a request returning a type specified by the
 * classToReturn parameter in #processRequest by proxifying
 * the resulting hyper resource
 */
public class HyperRequestProcessor {

    private static final ProfileResourceRegistryRetrievalStrategy PROFILE_RESOURCE_REGISTRY_RETRIEVAL_STRATEGY =
            new ProfileResourceRegistryRetrievalStrategy();

    private final ResourceRegistry resourceRegistry;
    private final RequestInterceptors requestInterceptors;
    private final HyperClient hyperClient;
    private final ResourceMethodInfoCache resourceMethodInfoCache;
    private final Map<String, MediaTypeHandler> mediaTypeHandlers = new HashMap<String, MediaTypeHandler>();
    private final ErrorHandler errorHandler;

    public HyperRequestProcessor(RootResourceBuilder rootResourceBuilder) {

        this.hyperClient = rootResourceBuilder.getHyperClient();
        if (this.hyperClient == null) {
            throw new IllegalArgumentException("Hyper Client must be provided");
        }

        this.errorHandler = rootResourceBuilder.getErrorHandler();
        if (this.errorHandler == null) {
            throw new IllegalArgumentException("Error Handler must be provided");
        }

        this.resourceMethodInfoCache = rootResourceBuilder.getResourceMethodInfoCache();
        if (this.resourceMethodInfoCache == null) {
            throw new IllegalArgumentException("Hyper Client must be provided");
        }

        //Copy the over so nobody can put more in there using the builder
        this.mediaTypeHandlers.putAll(rootResourceBuilder.mediaTypeHandlers);


        //TODO: this should be cloned/frozen so it's immutable and nobody can add stuff to the registry after all this is figured out
        this.resourceRegistry = rootResourceBuilder.getResourceRegistry();
        if (this.resourceRegistry == null) {
            throw new IllegalArgumentException("Root Resource Builder must be provided");
        }

        if (rootResourceBuilder.getRequestInterceptors() == null) {
            throw new IllegalArgumentException("Request Interceptors must be provided");
        }
        this.requestInterceptors = new RequestInterceptors(rootResourceBuilder.getRequestInterceptors());


    }

    /**
     * <p>Obtains a specific resource by going directly to its source.</p>
     * <p>The other way it's by fetching the root first and from there
     * following the hyper links until reaching the desired resource.</p>
     *
     * @param classToReturn  the class that the resource should be returned as
     * @param requestBuilder request object
     * @return resource with same type specified in the resource class.
     */
    public <T> T processRequest(Class<T> classToReturn, Request.RequestBuilder requestBuilder, TypeInfo typeInfo) {
        /*
         * Handle Get and other methods differently. Retrieve from cache first for Get
           and reload cache for any other methods. Keep the structure while removing cache for now.
         */
        // TODO: decide cache implementation.
//        if (requestBuilder.getMethod() == Method.GET) {
          return doProcessRequest(classToReturn, requestBuilder, typeInfo);
//        }
    }

    /**
     * Processes resource through proxy.
     *
     * @param requestBuilder request object
     * @return resource with same type specified in the resource class.
     * Keep the place holder of reload to future cache implementation
     * TODO: determine cache implementation so we reload when reload=true.
     */
    protected <T> T doProcessRequest(Class<T> classToReturn, Request.RequestBuilder requestBuilder, TypeInfo typeInfo) {

        requestInterceptors.intercept(requestBuilder);

        Request request = requestBuilder.build();

        Response response = hyperClient.execute(request);

        if (Response.class.isAssignableFrom(classToReturn)) {
            return (T) response;
        }
        if (String.class.isAssignableFrom(classToReturn)) {
            return (T) response.getBody();
        }

        return processResource(classToReturn, buildHyperResource(response), typeInfo);
    }

    /**
     * Creates a dynamic proxy that wraps a hyper resource.
     *
     * @param classToReturn the interface the proxy should implement
     * @param hyperResource resource to proxify
     * @return resource with same type specified in the resource class.
     */
    public <T> T processResource(Class<T> classToReturn, HyperResource hyperResource, TypeInfo typeInfo) {

        if (String.class.isAssignableFrom(classToReturn)) {
            return (T) hyperResource.toString();
        }

        InvocationHandler handler = new HyperResourceInvokeHandler(hyperResource, this, this.resourceMethodInfoCache.get(classToReturn), typeInfo);

        classToReturn = convertToSubClass(classToReturn, hyperResource);

        Object proxy = Proxy.newProxyInstance(
                classToReturn.getClassLoader(),
                new Class<?>[]{classToReturn},
                handler);

        return ReflectUtils.cast(classToReturn, proxy);
    }


    /**
     * converts a class to one of its sub classes, which is obtained from the last profile in the hyper resource
     */
    private <T> Class<T> convertToSubClass(Class<T> type, HyperResource hyperResource) {
        Class possibleSubClass = resourceRegistry.getResourceClass(PROFILE_RESOURCE_REGISTRY_RETRIEVAL_STRATEGY, Pair.with(type, hyperResource));
        return (possibleSubClass == null) ? type : possibleSubClass;
    }


    //builds the a hyper resource from a hyper response. Exceptions are handled by
    protected HyperResource buildHyperResource(Response response) {

        String contentType = response.getContentType();

        ResponseError hyperError = null;

        if (contentType != null) {
            /*Get required response handler according to response content type header.
            Here we are cleaning the content type removing the charset so it doesn't
            reject responses where charset is added (i.e. application/hal+json;charset=utf-8)
            Should we care more about charset???*/
            MediaTypeHandler mediaTypeHandler = this.mediaTypeHandlers.get(
                MediaTypeHelper.getContentTypeWithoutCharset(contentType));

            if (mediaTypeHandler != null) { // If we have an available handler for the response type.
                if (response.isOK()) { // If response is OK (200 status)
                    // Handle response.
                    return mediaTypeHandler.handleHyperResponse(response);
                } else { // Response is not OK (error code returned).
                    hyperError = mediaTypeHandler.parseError(response);
                }

            } else {
                hyperError = new ResponseError(response.getCode(), Messages.MSG_ERROR_HYPER_MEDIA_TYPE_HANDLER_NOT_FOUND_FOR_CONTENT_TYPE, contentType);
            }
        } else {
            hyperError = new ResponseError(response.getCode(), Messages.MSG_ERROR_NO_CONTENT_TYPE);
        }

        if (hyperError == null) {
            hyperError = new ResponseError(response.getCode(), Messages.MSG_ERROR_RESOURCE_CANNOT_BE_BUILT);
        }

        throw this.errorHandler.handleError(hyperError);
    }

}
