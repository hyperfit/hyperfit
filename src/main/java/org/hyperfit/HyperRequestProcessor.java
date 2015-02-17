package org.hyperfit;


import org.hyperfit.errorhandler.ErrorHandler;
import org.hyperfit.net.*;
import org.hyperfit.mediatype.MediaTypeHandler;

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
import java.util.Collections;
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
     *
     * @param classToReturn  the class that the resource should be returned as
     * @param requestBuilder request object
     * @return resource with same type specified in the resource class.
     */
    public <T> T processRequest(Class<T> classToReturn, Request.RequestBuilder requestBuilder, TypeInfo typeInfo) {

        requestInterceptors.intercept(requestBuilder);

        Request request = requestBuilder.build();

        Response response = hyperClient.execute(request);

        //Special case, if what they want is the Response in a raw format...well they can have it!
        if (Response.class.isAssignableFrom(classToReturn)) {
            return (T) response;
        }

        //Another special case, if what they want is a string we give them response body
        if (String.class.isAssignableFrom(classToReturn)) {
            return (T) response.getBody();
        }

        return processResource(classToReturn, buildHyperResource(request, response, classToReturn), typeInfo);
    }

    /**
     * Creates a dynamic proxy that wraps a hyper resource.
     *
     * @param classToReturn the interface the proxy should implement
     * @param hyperResource resource to proxify
     * @return resource with same type specified in the resource class.
     */
    public <T> T processResource(Class<T> classToReturn, HyperResource hyperResource, TypeInfo typeInfo) {

        //This can happen if they ask for the String of an embedded resource...not sure that i like that we parse it before
        //But it makes sense.  Note that if we made a request they don't get here because the String case is caught above
        //to skip the parsing of the response into a mediatype
        if (String.class.isAssignableFrom(classToReturn)) {
            return (T) hyperResource.toString();
        }


        InvocationHandler handler = new HyperResourceInvokeHandler(hyperResource, this, this.resourceMethodInfoCache.get(classToReturn), typeInfo);

        classToReturn = convertToSubClass(classToReturn, hyperResource);

        Object proxy = Proxy.newProxyInstance(
            classToReturn.getClassLoader(),
            new Class<?>[]{classToReturn},
            handler
        );

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
    protected <T> HyperResource buildHyperResource(Request request, Response response, Class<T> expectedResourceInterface) {

        //STAGE 1 - There's response, let's see if we understand the content type!

        String contentType = response.getContentType();
        //TODO COMAPI-1749 - this selection should be more rigorous and treat contentType not as a string
        contentType = MediaTypeHelper.getContentTypeWithoutCharset(contentType);
        //See if we have a content type, if not throw
        if(contentType == null || !this.mediaTypeHandlers.containsKey(contentType)){
            //We don't understand the content type, let's ask the error handler what to do!
            return this.errorHandler.unhandledContentType(
                request,
                response,
                Collections.unmodifiableMap(this.mediaTypeHandlers),
                expectedResourceInterface
            );
        }


        //STAGE 2 - There's a content type we understand, let's try to parse the response!

        MediaTypeHandler mediaTypeHandler = this.mediaTypeHandlers.get(contentType);
        HyperResource resource;
        try{
            resource = mediaTypeHandler.parseHyperResponse(response);
            //TODO: should we check for null here and throw?
        } catch (Exception e){
            //Something went wrong parsing the response, let's ask the error handler what to do!
            return this.errorHandler.contentParseError(
                request,
                response,
                Collections.unmodifiableMap(this.mediaTypeHandlers),
                expectedResourceInterface,
                e
            );
        }


        //STAGE 3 - we were able to parse the response into a HyperResponse, let's make sure it's a OK response
        if(!response.isOK()){
            return this.errorHandler.notOKResponse(
                request,
                response,
                Collections.unmodifiableMap(this.mediaTypeHandlers),
                expectedResourceInterface,
                resource
            );
        }


        //Everything with the resource worked out, let's return it
        return resource;
    }

}
