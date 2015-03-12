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
import org.hyperfit.utils.Preconditions;
import org.hyperfit.utils.StringUtils;

import java.util.Collection;

import static org.hyperfit.utils.MoreObjects.firstNonNull;

/**
 * Builder class for constructing HyperClient.
 */
public class HyperResourceInvocationContext {

    private final HyperClient hyperClient;
    private final ContentRegistry contentRegistry;
    private final ErrorHandler errorHandler;
    private final ResourceMethodInfoCache resourceMethodInfoCache;
    private final RequestInterceptors requestInterceptors;
    private final ResourceRegistry resourceRegistry;

    private HyperResourceInvocationContext(Builder builder) {
        contentRegistry = Preconditions.checkNotNull(builder.contentRegistry);
        hyperClient = Preconditions.checkNotNull(builder.hyperClient, "HyperClient cannot be null");
        errorHandler = firstNonNull(builder.errorHandler, new DefaultErrorHandler());
        resourceMethodInfoCache = firstNonNull(builder.resourceMethodInfoCache, new ConcurrentHashMapResourceMethodInfoCache());
        requestInterceptors = firstNonNull(builder.requestInterceptors, new RequestInterceptors());
        resourceRegistry = firstNonNull(builder.resourceRegistry, new ResourceRegistry(new ProfileResourceRegistryIndexStrategy()));
    }

    /**
     * Calls the passed endpoing and deserializes the result and returns as requested class
     *
     * @param classToReturn
     *          Type of HyperResource to Return
     * @param endpointURL
     *          URL to request
     * @return
     *          Hydrated hyper resource
     */
    public <T extends HyperResource> T invoke(Class<T> classToReturn, String endpointURL) {

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

    public ContentRegistry getContentRegistry() {
        return contentRegistry;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public HyperClient getHyperClient() {
        return hyperClient;
    }

    public RequestInterceptors getRequestInterceptors() {
        return requestInterceptors;
    }

    public ResourceMethodInfoCache getResourceMethodInfoCache() {
        return resourceMethodInfoCache;
    }

    public ResourceRegistry getResourceRegistry() {
        return resourceRegistry;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private  ContentRegistry contentRegistry = new ContentRegistry();
        private  HyperClient hyperClient;
        private  ErrorHandler errorHandler;
        private  ResourceMethodInfoCache resourceMethodInfoCache;
        private  RequestInterceptors requestInterceptors;
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

        public HyperResourceInvocationContext build() {
            return new HyperResourceInvocationContext(this);
        }
    }

}