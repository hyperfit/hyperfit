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
import org.hyperfit.resource.registry.ResourceRegistry;
import org.hyperfit.utils.StringUtils;

import java.util.Collection;

/**
 * Builder class for constructing HyperClient.
 */
public class RootResourceBuilder {

    // TODO: determine cache strategy.
    //private HyperCacheStrategy hyperCache = new HyperCacheNoop();
    private HyperClient hyperClient;
    private final ContentRegistry contentRegistry = new ContentRegistry();
    private ErrorHandler errorHandler = new DefaultErrorHandler();
    private ResourceMethodInfoCache resourceMethodInfoCache = new ConcurrentHashMapResourceMethodInfoCache();
    private RequestInterceptors requestInterceptors = new RequestInterceptors();
    private ResourceRegistry resourceRegistry = new ResourceRegistry(new ProfileResourceRegistryIndexStrategy());

    public RootResourceBuilder(HyperClient hyperClient) {
        this.hyperClient = hyperClient;
    }

    public RootResourceBuilder addContentTypeHandler(ContentTypeHandler handler) {
        this.contentRegistry.add(handler);
        return this;
    }

    public RootResourceBuilder removeContentTypeHandler(ContentTypeHandler handler) {
        this.contentRegistry.remove(handler);
        return this;
    }

    public RootResourceBuilder addContentTypeHandler(ContentTypeHandler handler, ContentType...types) {
        this.contentRegistry.add(handler, types);
        return this;
    }

    public RootResourceBuilder removeContentType(ContentType type) {
        this.contentRegistry.remove(type);
        return this;
    }

    public ContentRegistry getContentRegistry() {
        return this.contentRegistry;
    }


    public RootResourceBuilder hyperClient(HyperClient hyperClient) {
        this.hyperClient = hyperClient;
        return this;
    }

    public HyperClient getHyperClient() {
        return hyperClient;
    }

    public RootResourceBuilder errorHandler(ErrorHandler errorHandler) {
        if (errorHandler != null) {
            // Default handler should always be present in case there is no custom one.
            this.errorHandler = errorHandler;
        }
        return this;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }


    public void resourceMethodInfoCache(ResourceMethodInfoCache resourceMethodInfoCache) {
        if (resourceMethodInfoCache != null) {
            this.resourceMethodInfoCache = resourceMethodInfoCache;
        }
    }

    public ResourceMethodInfoCache getResourceMethodInfoCache() {
        return resourceMethodInfoCache;
    }


    public RootResourceBuilder addRequestInterceptor(RequestInterceptor requestInterceptor) {
        this.requestInterceptors.add(requestInterceptor);
        return this;
    }

    public RootResourceBuilder removeRequestInterceptors(RequestInterceptor... requestInterceptor) {
        this.requestInterceptors.remove(requestInterceptor);
        return this;
    }

    public RequestInterceptors getRequestInterceptors() {
        return requestInterceptors;
    }

    public RootResourceBuilder clearInterceptors() {
        this.requestInterceptors.clear();
        return this;
    }


    public ResourceRegistry getResourceRegistry() {
        return resourceRegistry;
    }


    public RootResourceBuilder resourceRegistry(ResourceRegistry resourceRegistry) {
        this.resourceRegistry = resourceRegistry;
        return this;
    }


    public RootResourceBuilder registerResources(Collection<Class<? extends HyperResource>> classes) {
        this.resourceRegistry.add(classes);
        return this;
    }


    public <T extends HyperResource> T build(Class<T> classToReturn, String endpointURL) {
        if (hyperClient == null){
            throw new IllegalStateException("hyperClient cannot be null");
        }

        if(StringUtils.isEmpty(endpointURL)){
            throw new IllegalArgumentException("endpointURL can not be null or empty");
        }

        if(classToReturn == null){
            throw new IllegalArgumentException("classToReturn can not be null");
        }

        hyperClient.setAcceptedContentTypes(this.contentRegistry.getResponseParsingContentTypes());
        HyperRequestProcessor requestProcessor = new HyperRequestProcessor(this);
        return requestProcessor.processRequest(classToReturn, BoringRequestBuilder.get(endpointURL), null);
    }


}