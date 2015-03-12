package org.hyperfit;


import org.hyperfit.content.ContentRegistry;
import org.hyperfit.content.ContentType;
import org.hyperfit.content.ContentTypeHandler;
import org.hyperfit.errorhandler.DefaultErrorHandler;
import org.hyperfit.errorhandler.ErrorHandler;
import org.hyperfit.methodinfo.ConcurrentHashMapResourceMethodInfoCache;
import org.hyperfit.methodinfo.ResourceMethodInfoCache;
import org.hyperfit.net.*;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.registry.ProfileResourceRegistryIndexStrategy;
import org.hyperfit.resource.registry.ProfileResourceRegistryRetrievalStrategy;
import org.hyperfit.resource.registry.ResourceRegistry;
import org.hyperfit.utils.Preconditions;
import org.hyperfit.utils.ReflectUtils;
import org.hyperfit.utils.TypeInfo;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Collection;

import static org.hyperfit.utils.MoreObjects.firstNonNull;

/**
 * Processes a request returning a type specified by the
 * classToReturn parameter in #processRequest by proxifying
 * the resulting hyper resource
 */
public class HyperRequestProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(HyperRequestProcessor.class);

    private static final ProfileResourceRegistryRetrievalStrategy PROFILE_RESOURCE_REGISTRY_RETRIEVAL_STRATEGY =
            new ProfileResourceRegistryRetrievalStrategy();

    private final ResourceRegistry resourceRegistry;
    private final RequestInterceptors requestInterceptors;
    private final HyperClient hyperClient;
    private final ResourceMethodInfoCache resourceMethodInfoCache;
    //TODO: make this protected hack non-sense go away...something is wrong with our class layout
    protected final ContentRegistry contentRegistry;
    private final ErrorHandler errorHandler;

    private HyperRequestProcessor(Builder builder) {

        contentRegistry = Preconditions.checkNotNull(builder.contentRegistry);
        hyperClient = Preconditions.checkNotNull(builder.hyperClient, "HyperClient cannot be null");
        errorHandler = firstNonNull(builder.errorHandler, new DefaultErrorHandler());
        resourceMethodInfoCache = firstNonNull(builder.resourceMethodInfoCache, new ConcurrentHashMapResourceMethodInfoCache());
        requestInterceptors = firstNonNull(builder.requestInterceptors, new RequestInterceptors());
        resourceRegistry = firstNonNull(builder.resourceRegistry, new ResourceRegistry(new ProfileResourceRegistryIndexStrategy()));

        hyperClient.setAcceptedContentTypes(contentRegistry.getResponseParsingContentTypes());
    }


    /**
     * <p>Obtains a specific resource by going directly to its source.</p>
     *
     * @param classToReturn  the class that the resource should be returned as
     * @param endpointUrl endpont url for the request
     * @return resource with same type specified in the resource class.
     */
    public <T> T processRequest(Class<T> classToReturn, String endpointUrl){
        return processRequest(classToReturn, BoringRequestBuilder.get(endpointUrl));
    }


    /**
     * <p>Obtains a specific resource by going directly to its source.</p>
     *
     * @param classToReturn  the class that the resource should be returned as
     * @param requestBuilder request object
     * @return resource with same type specified in the resource class.
     */
    public <T> T processRequest(Class<T> classToReturn, RequestBuilder requestBuilder){
        return processRequest(classToReturn, requestBuilder, null);
    }


    /**
     * <p>Obtains a specific resource by going directly to its source.</p>
     *
     * @param classToReturn  the class that the resource should be returned as
     * @param requestBuilder request object
     * @return resource with same type specified in the resource class.
     */
    @SuppressWarnings("unchecked")
    public <T> T processRequest(Class<T> classToReturn, RequestBuilder requestBuilder, TypeInfo typeInfo) {

        if(requestBuilder == null){
            throw new IllegalArgumentException("requestBuilder can not be null");
        }

        if(classToReturn == null){
            throw new IllegalArgumentException("classToReturn can not be null");
        }

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

        ContentType responseContentType = null;
        try {
            responseContentType = ContentType.parse(response.getContentType());
        } catch (Exception e){
            LOG.warn("Error parsing content type of response.  errorHandler:unhandledContentType will be called", e);
        }

        //See if we have a content type, if not throw
        if(responseContentType == null || !this.contentRegistry.canHandler(responseContentType, ContentRegistry.Purpose.PARSE_RESPONSE)){
            //We don't understand the content type, let's ask the error handler what to do!
            return this.errorHandler.unhandledContentType(
                request,
                response,
                this.contentRegistry,
                expectedResourceInterface
            );
        }


        //STAGE 2 - There's a content type we understand, let's try to parse the response!

        ContentTypeHandler contentTypeHandler = this.contentRegistry.getHandler(responseContentType, ContentRegistry.Purpose.PARSE_RESPONSE);
        HyperResource resource;
        try{
            resource = contentTypeHandler.parseResponse(response);
            //TODO: should we check for null here and throw?
        } catch (Exception e){
            //Something went wrong parsing the response, let's ask the error handler what to do!
            return this.errorHandler.contentParseError(
                request,
                response,
                this.contentRegistry,
                expectedResourceInterface,
                e
            );
        }


        //STAGE 3 - we were able to parse the response into a HyperResponse, let's make sure it's a OK response
        if(!response.isOK()){
            return this.errorHandler.notOKResponse(
                request,
                response,
                this.contentRegistry,
                expectedResourceInterface,
                resource
            );
        }


        //Everything with the resource worked out, let's return it
        return resource;
    }


    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {

        private  ContentRegistry contentRegistry = new ContentRegistry();
        private  HyperClient hyperClient;
        private  ErrorHandler errorHandler;
        private  ResourceMethodInfoCache resourceMethodInfoCache;
        private  RequestInterceptors requestInterceptors = new RequestInterceptors();
        private  ResourceRegistry resourceRegistry;

        public Builder addContentTypeHandler(ContentTypeHandler handler) {
            this.contentRegistry.add(handler);
            return this;
        }

        public Builder removeContentTypeHandler(ContentTypeHandler handler) {
            this.contentRegistry.remove(handler);
            return this;
        }

        public Builder addContentTypeHandler(ContentTypeHandler handler, ContentType...types) {
            this.contentRegistry.add(handler, types);
            return this;
        }

        public Builder removeContentType(ContentType type) {
            this.contentRegistry.remove(type);
            return this;
        }

        public Builder hyperClient(HyperClient hyperClient) {
            this.hyperClient = hyperClient;
            return this;
        }

        public Builder errorHandler(ErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        public Builder resourceMethodInfoCache(ResourceMethodInfoCache resourceMethodInfoCache) {
            this.resourceMethodInfoCache = resourceMethodInfoCache;
            return this;
        }

        public Builder addRequestInterceptor(RequestInterceptor requestInterceptor) {
            this.requestInterceptors.add(requestInterceptor);
            return this;
        }

        public Builder removeRequestInterceptors(RequestInterceptor... requestInterceptor) {
            this.requestInterceptors.remove(requestInterceptor);
            return this;
        }

        public Builder clearInterceptors() {
            this.requestInterceptors.clear();
            return this;
        }
        public Builder resourceRegistry(ResourceRegistry resourceRegistry) {
            this.resourceRegistry = resourceRegistry;
            return this;
        }

        public Builder registerResources(Collection<Class<? extends HyperResource>> classes) {
            this.resourceRegistry.add(classes);
            return this;
        }

        public HyperRequestProcessor build() {
            return new HyperRequestProcessor(this);
        }
    }

}



