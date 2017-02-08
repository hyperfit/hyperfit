package org.hyperfit;


import lombok.NonNull;
import org.hyperfit.content.ContentRegistry;
import org.hyperfit.content.ContentType;
import org.hyperfit.content.ContentTypeHandler;
import org.hyperfit.errorhandler.DefaultErrorHandler;
import org.hyperfit.errorhandler.ErrorHandler;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.exception.NoClientRegisteredForSchemeException;
import org.hyperfit.handlers.Java8DefaultMethodHandler;
import org.hyperfit.message.Messages;
import org.hyperfit.methodinfo.ConcurrentHashMapResourceMethodInfoCache;
import org.hyperfit.methodinfo.ResourceMethodInfoCache;
import org.hyperfit.net.*;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.InterfaceSelectionStrategy;
import org.hyperfit.resource.SimpleInterfaceSelectionStrategy;
import org.hyperfit.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hyperfit.utils.MoreObjects.firstNonNull;

/**
 * Processes a request returning a type specified by the
 * classToReturn parameter in #processRequest by proxifying
 * the resulting hyper resource
 */
public class HyperfitProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(HyperfitProcessor.class);


    private final RequestInterceptors requestInterceptors;
    private final ResourceMethodInfoCache resourceMethodInfoCache;
    //TODO: make this protected hack non-sense go away...something is wrong with our class layout
    protected final ContentRegistry contentRegistry;
    private final ErrorHandler errorHandler;
    private final InterfaceSelectionStrategy interfaceSelectionStrategy;
    private final Map<String, HyperClient> schemeClientMap;
    private final Java8DefaultMethodHandler java8DefaultMethodHandler;

    private HyperfitProcessor(Builder builder) {

        contentRegistry = Preconditions.checkNotNull(builder.contentRegistry);
        errorHandler = firstNonNull(builder.errorHandler, new DefaultErrorHandler());
        resourceMethodInfoCache = firstNonNull(builder.resourceMethodInfoCache, new ConcurrentHashMapResourceMethodInfoCache());
        requestInterceptors = firstNonNull(builder.requestInterceptors, new RequestInterceptors());
        interfaceSelectionStrategy =  Preconditions.checkNotNull(builder.interfaceSelectionStrategy);
        java8DefaultMethodHandler = Preconditions.checkNotNull(builder.java8DefaultMethodHandler);

        if(builder.schemeClientMap == null || builder.schemeClientMap.size() == 0){
            throw new NoClientRegisteredForSchemeException(Messages.MSG_ERROR_NO_CLIENT);
        }
        schemeClientMap = builder.schemeClientMap;

        //get the distinct hyperclient from the map and call the setAcceptedContentTypes
        Set<HyperClient> uniqueClient = new HashSet<HyperClient>();
        for(HyperClient hyperClient: schemeClientMap.values()){
            uniqueClient.add(hyperClient);
        }
        for(HyperClient hyperClient: uniqueClient){
            hyperClient.setAcceptedContentTypes(contentRegistry.getResponseParsingContentTypes());
        }
    }


    /**
     * <p>Obtains a specific resource by going directly to its source.</p>
     *
     * @param classToReturn  the class that the resource should be returned as
     * @param entryPointURL a url to an entry point of the RESTful service
     * @return resource with same type specified in the resource class.
     */
    public <T> T processRequest(Class<T> classToReturn, String entryPointURL){
        if(StringUtils.isEmpty(entryPointURL)){
            throw new IllegalArgumentException("entryPointURL can not be null or empty");
        }
        return processRequest(classToReturn, BoringRequestBuilder.get(entryPointURL));
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
     * <p>Obtains a specific resource by going directly to its source using super type tokens so a generic can be returned.</p>
     *
     * @param typeToReturn a super type token
     * @param requestBuilder request object
     * @return resource with same type specified in the resource class.
     */
    public <T> T processRequest(TypeRef<T> typeToReturn, RequestBuilder requestBuilder){
        if(typeToReturn == null){
            throw new IllegalArgumentException("typeToReturn can not be null");
        }
        return processRequest(typeToReturn.getClazz(), requestBuilder, new TypeInfo().make(typeToReturn.getType()));
    }

    /**
     * <p>Obtains a specific resource by going directly to its source using super type tokens so a generic can be returned.</p>
     *
     * @param typeToReturn a super type token
     * @param entryPointURL request object
     * @return resource with same type specified in the resource class.
     */
    public <T> T processRequest(TypeRef<T> typeToReturn, String entryPointURL){
        if(StringUtils.isEmpty(entryPointURL)){
            throw new IllegalArgumentException("entryPointURL can not be null or empty");
        }
        return processRequest(typeToReturn, BoringRequestBuilder.get(entryPointURL));
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

        if(classToReturn == null){
            throw new IllegalArgumentException("classToReturn can not be null");
        }

        if(requestBuilder == null){
            throw new IllegalArgumentException("requestBuilder can not be null");
        }


        requestInterceptors.intercept(requestBuilder);

        Request request = requestBuilder.build();
        //find the scheme
        int pos = request.getUrl().indexOf(":");

        String scheme = null;
        if(pos > 0) {
            scheme = request.getUrl().substring(0, pos);
        }

        if(StringUtils.isEmpty(scheme)){
            throw new IllegalArgumentException("The request url does not have a scheme");
        }

        //TODO: return the request if that's what they want


        HyperClient hyperClient = schemeClientMap.get(scheme);
        if(hyperClient == null){
            Set<String> registeredSchemes = schemeClientMap.keySet();
            throw new NoClientRegisteredForSchemeException(Messages.MSG_ERROR_NO_CLIENT_FOR_SCHEME, scheme, registeredSchemes);
        }

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


        InvocationHandler handler = new HyperResourceInvokeHandler(
            hyperResource,
            this,
            this.resourceMethodInfoCache.get(classToReturn),
            typeInfo,
            java8DefaultMethodHandler
        );


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
        private ErrorHandler errorHandler;
        private ResourceMethodInfoCache resourceMethodInfoCache;
        private RequestInterceptors requestInterceptors = new RequestInterceptors();
        private InterfaceSelectionStrategy interfaceSelectionStrategy = new SimpleInterfaceSelectionStrategy();
        private Map<String, HyperClient> schemeClientMap = new HashMap<String, HyperClient>();
        private Java8DefaultMethodHandler java8DefaultMethodHandler = new Java8DefaultMethodHandler() {
            public Object invoke(@NonNull DefaultMethodContext context, Object[] args) {
                throw new HyperfitException("No Java8DefaultMethodHandler implementation specified.  Are you missing a call to the HyperfitProcessor builder?");
            }
        };

        public Builder addContentTypeHandler(ContentTypeHandler handler) {
            this.contentRegistry.add(handler);
            return this;
        }

        public Builder addContentTypeHandler(ContentTypeHandler handler, double q) {
            this.contentRegistry.add(handler, handler.getDefaultContentType().withQ(q));
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

        /**
         * A HyperClient will be registered in schemeClientMap based on the schemes it can handle, which is defined in getSchemes()
         * @param hyperClient HyperClient {@link org.hyperfit.net.HyperClient}
         * @return {@link org.hyperfit.HyperfitProcessor.Builder}
         */
        public Builder hyperClient(HyperClient hyperClient) {
            if( hyperClient == null){
                throw new IllegalArgumentException("HyperClient can not be null");
            }
            for(String scheme: hyperClient.getSchemes()){
                schemeClientMap.put(scheme, hyperClient);
            }
            return this;
        }

        /**
         *  A HyperClient will be registered in schemeClientMap based on the schemes it provided in the parameters
         *   and ignores the default schemes registered by getSchemes()
         * @param hyperClient {@link org.hyperfit.net.HyperClient}
         * @param schemes {@link java.lang.String}
         * @return {@link org.hyperfit.HyperfitProcessor.Builder}
         */
        public Builder hyperClient(HyperClient hyperClient, String... schemes){
            boolean isSchemeValid = false;
            for(String scheme: schemes){
                if(!StringUtils.isEmpty(scheme)) {
                    isSchemeValid = true;
                    break;
                }
            }
            if(!isSchemeValid) {
                throw new IllegalArgumentException("HyperClient has to have schemes defined");
            }

            for(String scheme: schemes){
                schemeClientMap.put(scheme, hyperClient);
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

        public Builder removeRequestInterceptors(Class<? extends RequestInterceptor> typeToRemove) {
            this.requestInterceptors.remove(typeToRemove);
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

        public Builder defaultMethodInvoker(Java8DefaultMethodHandler methodInvoker) {
            if( methodInvoker == null){
                throw new IllegalArgumentException("methodInvoker can not be null");
            }

            this.java8DefaultMethodHandler = methodInvoker;

            return this;
        }

        public HyperfitProcessor build() {
            return new HyperfitProcessor(this);
        }
    }

}



