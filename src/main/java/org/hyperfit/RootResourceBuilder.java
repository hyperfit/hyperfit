package org.hyperfit;


import org.hyperfit.errorhandler.DefaultErrorHandler;
import org.hyperfit.errorhandler.ErrorHandler;
import org.hyperfit.net.HyperClient;
import org.hyperfit.net.Request;
import org.hyperfit.net.RequestInterceptor;
import org.hyperfit.net.RequestInterceptors;
import org.hyperfit.net.okhttp.OkHttpHyperClient;
import org.hyperfit.mediatype.MediaTypeHandler;
import org.hyperfit.methodinfo.ConcurrentHashMapResourceMethodInfoCache;
import org.hyperfit.methodinfo.ResourceMethodInfoCache;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.registry.ProfileResourceRegistryIndexStrategy;
import org.hyperfit.resource.registry.ResourceRegistry;
import org.hyperfit.utils.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Builder class for constructing HyperClient.
 */
public class RootResourceBuilder {

    // TODO: determine cache strategy.
    //private HyperCacheStrategy hyperCache = new HyperCacheNoop();
    private HyperClient hyperClient = new OkHttpHyperClient();
    protected final Map<String, MediaTypeHandler> mediaTypeHandlers = new HashMap<String, MediaTypeHandler>();
    private ErrorHandler errorHandler = new DefaultErrorHandler();
    private ResourceMethodInfoCache resourceMethodInfoCache = new ConcurrentHashMapResourceMethodInfoCache();
    private RequestInterceptors requestInterceptors = new RequestInterceptors();
    private ResourceRegistry resourceRegistry = new ResourceRegistry(new ProfileResourceRegistryIndexStrategy());


    public RootResourceBuilder addMediaTypeHandler(MediaTypeHandler mediaType) {
        this.mediaTypeHandlers.put(mediaType.getDefaultHandledMediaType(), mediaType);
        return this;
    }

    public RootResourceBuilder removeMediaTypeHandler(MediaTypeHandler mediaType) {
        this.mediaTypeHandlers.remove(mediaType.getDefaultHandledMediaType());
        return this;
    }

    public RootResourceBuilder addMediaTypeHandler(String mediaType, MediaTypeHandler mediaTypeHandler) {
        this.mediaTypeHandlers.put(mediaType, mediaTypeHandler);
        return this;
    }

    public RootResourceBuilder removeMediaTypeHandler(String mediaType) {
        this.mediaTypeHandlers.remove(mediaType);
        return this;
    }

    public MediaTypeHandler getMediaTypeHandler(String mediaType) {
        return mediaTypeHandlers.get(mediaType);
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
        if(StringUtils.isEmpty(endpointURL)){
            throw new IllegalArgumentException("endpointURL can not be null or empty");
        }

        if(classToReturn == null){
            throw new IllegalArgumentException("classToReturn can not be null");
        }

        hyperClient.setAcceptedContentTypes(this.mediaTypeHandlers.keySet());
        HyperRequestProcessor requestProcessor = new HyperRequestProcessor(this);
        return requestProcessor.processRequest(classToReturn, Request.get(endpointURL), null);
    }


}