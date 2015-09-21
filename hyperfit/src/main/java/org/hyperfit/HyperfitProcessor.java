package org.hyperfit;


import org.hyperfit.content.ContentRegistry;
import org.hyperfit.content.ContentType;
import org.hyperfit.content.ContentTypeHandler;
import org.hyperfit.errorhandler.DefaultErrorHandler;
import org.hyperfit.errorhandler.ErrorHandler;
import org.hyperfit.exception.NoClientRegisteredForScheme;
import org.hyperfit.message.Messages;
import org.hyperfit.methodinfo.ConcurrentHashMapResourceMethodInfoCache;
import org.hyperfit.methodinfo.ResourceMethodInfoCache;
import org.hyperfit.net.*;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.InterfaceSelectionStrategy;
import org.hyperfit.resource.SimpleInterfaceSelectionStrategy;
import org.hyperfit.utils.Preconditions;
import org.hyperfit.utils.ReflectUtils;
import org.hyperfit.utils.TypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.hyperfit.utils.MoreObjects.firstNonNull;

/**
 * Processes a request returning a type specified by the
 * classToReturn parameter in #processRequest by proxifying
 * the resulting hyper resource
 */
public class HyperfitProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(HyperfitProcessor.class);


    private final RequestInterceptors requestInterceptors;
    private HyperClient hyperClient;
    private final ResourceMethodInfoCache resourceMethodInfoCache;
    //TODO: make this protected hack non-sense go away...something is wrong with our class layout
    protected final ContentRegistry contentRegistry;
    private final ErrorHandler errorHandler;
    private final InterfaceSelectionStrategy interfaceSelectionStrategy;
    private final Map<String, HyperClient> schemeClientMap;

    private HyperfitProcessor(Builder builder) {

        contentRegistry = Preconditions.checkNotNull(builder.contentRegistry);
        //hyperClient = Preconditions.checkNotNull(builder.hyperClient, "HyperClient cannot be null");
        errorHandler = firstNonNull(builder.errorHandler, new DefaultErrorHandler());
        resourceMethodInfoCache = firstNonNull(builder.resourceMethodInfoCache, new ConcurrentHashMapResourceMethodInfoCache());
        requestInterceptors = firstNonNull(builder.requestInterceptors, new RequestInterceptors());
        interfaceSelectionStrategy =  Preconditions.checkNotNull(builder.interfaceSelectionStrategy);

        //hyperClient.setAcceptedContentTypes(contentRegistry.getResponseParsingContentTypes());
        schemeClientMap = builder.schemeClientMap;
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
        //find the scheme
        int pos = request.getUrl().indexOf(":");

        String scheme = request.getUrl().substring(0, pos);

        //TODO: return the request if that's what they want


        hyperClient = schemeClientMap.get(scheme);
        if(hyperClient == null){
            throw new NoClientRegisteredForScheme(Messages.MSG_ERROR_NO_CLIENT_FOR_SCHEME, scheme);
        }
        hyperClient.setAcceptedContentTypes(contentRegistry.getResponseParsingContentTypes());

        Response response = hyperClient.execute(request);

        return processResponse(classToReturn, response, typeInfo);
    }

    public <T> T processResponse(Class<T> classToReturn, Response response, TypeInfo typeInfo) {

        //Special case, if what they want is the Response in a raw format...well they can have it!
        if (Response.class.isAssignableFrom(classToReturn)) {
            return (T) response;
        }

        //Another special case, if what they want is a string we give them response body
        //before we process it
        if (String.class.isAssignableFrom(classToReturn)) {
            return (T) response.getBody();
        }

        HyperResource resource = buildHyperResource(response, classToReturn);

        return processResource(classToReturn, resource, typeInfo);
    }

    /**
     * Creates a dynamic proxy that wraps a hyper resource.
     *
     * @param classToReturn the interface the proxy should implement
     * @param hyperResource resource to proxify
     * @return resource with same type specified in the resource class.
     */
    public <T> T processResource(Class<T> classToReturn, HyperResource hyperResource, TypeInfo typeInfo) {

        //TODO: if they just want a hyper resource, give it to them

        //This can happen if they ask for the String of an embedded resource...not sure that i like that we parse it before
        //But it makes sense.  Note that if we made a request they don't get here because the String case is caught above
        //to skip the parsing of the response into a mediatype
        if (String.class.isAssignableFrom(classToReturn)) {
            return (T) hyperResource.toString();
        }


        InvocationHandler handler = new HyperResourceInvokeHandler(hyperResource, this, this.resourceMethodInfoCache.get(classToReturn), typeInfo);


        Object proxy = Proxy.newProxyInstance(
            classToReturn.getClassLoader(),
            interfaceSelectionStrategy.determineInterfaces(classToReturn, hyperResource),
            handler
        );

        return ReflectUtils.cast(classToReturn, proxy);
    }





    //builds the a hyper resource from a hyper response. Exceptions are handled by
    protected <T> HyperResource buildHyperResource(Response response, Class<T> expectedResourceInterface) {

        //STAGE 1 - There's response, let's see if we understand the content type!

        ContentType responseContentType = null;
        try {
            responseContentType = ContentType.parse(response.getContentType());
        } catch (Exception e){
            LOG.warn("Error parsing content type of response.  errorHandler:unhandledContentType will be called", e);
        }

        //See if we have a content type, if not throw
        if(responseContentType == null || !this.contentRegistry.canHandle(responseContentType, ContentRegistry.Purpose.PARSE_RESPONSE)){
            //We don't understand the content type, let's ask the error handler what to do!
            return this.errorHandler.unhandledContentType(
                this,
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
                this,
                response,
                this.contentRegistry,
                expectedResourceInterface,
                e
            );
        }


        //STAGE 3 - we were able to parse the response into a HyperResponse, let's make sure it's a OK response
        if(!response.isOK()){
            return this.errorHandler.notOKResponse(
                this,
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

        private ContentRegistry contentRegistry = new ContentRegistry();
        private HyperClient hyperClient;
        private ErrorHandler errorHandler;
        private ResourceMethodInfoCache resourceMethodInfoCache;
        private RequestInterceptors requestInterceptors = new RequestInterceptors();
        private InterfaceSelectionStrategy interfaceSelectionStrategy = new SimpleInterfaceSelectionStrategy();
        private Map<String, HyperClient> schemeClientMap = new HashMap<String, HyperClient>();

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

            for(String scheme: hyperClient.getSchemas()){
                schemeClientMap.put(scheme, hyperClient);
            }
            return this;
        }

        public Builder hyperClient(HyperClient hyperClient, String... scheme){
            for(int i=0; i<scheme.length; i++){
                schemeClientMap.put(scheme[i], hyperClient);
            }
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

        public Builder interfaceSelectionStrategy(InterfaceSelectionStrategy selectionStrategy) {
            this.interfaceSelectionStrategy = selectionStrategy;
            return this;
        }



        public HyperfitProcessor build() {
            return new HyperfitProcessor(this);
        }
    }

}



