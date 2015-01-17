package org.hyperfit;

import org.hyperfit.exception.HyperClientException;
import org.hyperfit.http.Request;
import org.hyperfit.message.Messages;
import org.hyperfit.methodinfo.MethodInfo;
import org.hyperfit.methodinfo.MethodInfoCache;
import org.hyperfit.http.Request.RequestBuilder;
import org.hyperfit.resource.HyperLink;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.annotation.Data;
import org.hyperfit.annotation.Link;
import org.hyperfit.utils.Pair;
import org.hyperfit.utils.ReflectUtils;
import org.hyperfit.utils.TypeInfo;
import org.hyperfit.utils.TypeRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import static org.hyperfit.methodinfo.MethodInfo.MethodType;

/**
 * <p>This class performs all the mappings associated to annotated interfaces for resource interfaces</p>
 * <p>Retrieves values for annotated methods with @Data annotation, or follow hyper media links
 * for methods annotated with @Link annotation</p>
 */
public class HyperResourceInvokeHandler implements InvocationHandler {

    private static final String MESSAGE_EXCEPTION_SOLVING_MULTI_LINK = "Cannot resolve a multi link resource";

    private final HyperResource hyperResource;
    private final HyperRequestProcessor requestProcessor;

    private final MethodInfoCache methodInfoCache;
    private TypeInfo typeInfo;

    public HyperResourceInvokeHandler(HyperResource hyperResource, HyperRequestProcessor requestProcessor, MethodInfoCache methodInfoCache, TypeInfo typeInfo) {
        this.hyperResource = hyperResource;
        this.requestProcessor = requestProcessor;

        this.typeInfo = (typeInfo != null) ? typeInfo : new TypeInfo();

        this.methodInfoCache = methodInfoCache;
    }

    protected HyperLink extendHyperLink(HyperLink hyperLink) {
        return new HyperLink(hyperLink) {

            @Override
            public <R> R follow(TypeRef<R> typeRef) {

                if (typeRef == null) {
                    throw new IllegalArgumentException("type reference must be null");
                }

                Class<?> returnType = typeRef.getClazz();
                Type genericReturnType = typeRef.getType();

                String linkRelationship = this.getRel();

                //TODO: it sure seems like we can combine this logic with the stuff in Invoke
                //that respects the @Link annotation...but they turn out to be quite different
                //in respect to what they can and cannot grab form the _embedded resources
                //so we couldn't quite get it right...maybe someone can refactor it some day into
                //a common root..maybe when follow supports params?

                //If we can get it locally...do it!
                //Note we cannot resolve a a given link of a multi link relationship from embedded as there's no way to
                //identify which one is the link...thus we have to fetch it via request
                //TODO: there's an edge case where if we have a link from a multilink relationship that only has 1 link
                //and embedded has just 1, then we can return that link...but punting on this for  now.
                if (!hyperResource.isMultiLink(linkRelationship) && hyperResource.canResolveLinkLocal(linkRelationship)) {
                    return (R) processEmbeddedResource(
                            returnType,
                            genericReturnType,
                            hyperResource.resolveLinkLocal(linkRelationship)
                    );
                }

                RequestBuilder requestBuilder = this.toRequestBuilder();
                //TODO: is there a way to get rid of this R?
                return (R) requestProcessor.processRequest(returnType, requestBuilder, typeInfo.make(genericReturnType));
            }
        };
    }

    protected HyperLink[] extendHyperLinks(HyperLink[] hyperLinks) {
        for (int i = 0; i < hyperLinks.length; i++) {
            hyperLinks[i] = this.extendHyperLink(hyperLinks[i]);
        }
        return hyperLinks;
    }

    /**
     * Fulfills invocation of resource proxy methods
     *
     * @param proxy  proxy instance
     * @param method method to invoke
     * @param args   method params
     * @return object according to the proxy method invoked
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        try {
            return processInvoke(proxy, method, args);
        } catch (HyperClientException hce) {
            throw hce; //in case we threw it earlier
        } catch (Exception e) {
            throw new HyperClientException(e, Messages.MSG_ERROR_PROXY_UNEXPECTED_ERROR, method, proxy, args);
        }
    }


    protected <T> T processEmbeddedResources(Class<T> returnClass, Type genericReturnType, HyperResource[] hyperResources){
        //TODO: in the future this would actually be a strategy...not these if blocks

        if (returnClass.isArray()) {
            Pair<? extends Class<?>,Type> arrayTypeInfo = typeInfo.getArrayType(returnClass, genericReturnType);
            TypeInfo newInfo = typeInfo.make(arrayTypeInfo.getRight());

            Object[] result = ReflectUtils.createArray(arrayTypeInfo.getLeft(), hyperResources.length);

            for (int i = 0; i < hyperResources.length; i++) {
                result[i] =  this.requestProcessor.processResource(arrayTypeInfo.getLeft(), hyperResources[i], newInfo);
            }

            return (T)result;

        }

        /*
        if (List.class.isAssignableFrom(returnClass)) {
            return processReturnValueList((ParameterizedType) genericReturnType, hyperResourcePart);
        }
        */

        throw new RuntimeException("Can't deal with return type");
    }

    protected <T> T processEmbeddedResource(Class<T> returnClass, Type genericReturnType, HyperResource hyperResource){
        return this.requestProcessor.processResource(returnClass, hyperResource, typeInfo.make(genericReturnType));
    }


    /**
     * Processes invocation of resource proxy methods
     *
     * @param proxy  proxy instance
     * @param method method to invoke
     * @param args   method params
     * @return object according to the proxy method invoked
     */
    protected Object processInvoke(Object proxy, Method method, Object[] args) throws Exception {

        MethodInfo methodInfo = this.methodInfoCache.get(method);

        MethodType methodType = methodInfo.getMethodType();

        //Equals is handled very specially so we check for that first
        if (MethodType.EQUALS == methodType) {
            return determineEquals(args);
        }

        //This handler needs to capture follow methods so it can resolve invokes to follow
        if (MethodType.GET_LINK == methodType) {
            return this.extendHyperLink((HyperLink) method.invoke(this.hyperResource, args));
        }

        if (MethodType.GET_LINKS == methodType) {
           return this.extendHyperLinks((HyperLink[]) method.invoke(this.hyperResource, args));
        }

        //If it's a method on hyperResource just pass it on through
        if (MethodType.FROM_HYPER_RESOURCE_CLASS == methodType ||
                MethodType.HASH_CODE == methodType ||
                MethodType.TO_STRING == methodType) {

            return method.invoke(this.hyperResource, args);
        }

        // Data method invocation
        Data data = methodInfo.getDataAnnotation();
        if (data != null) {
            return hyperResource.getPathAs(methodInfo.getReturnType(), data.value());
        }

        // Link method invocation
        Link link = methodInfo.getLinkAnnotation();

        if (link != null) {
            String linkRelationship = link.value();
            String linkName = link.name();

            //NOTE: this seems a bit hacky, but will a link ever return a boolean? probably not
            if (boolean.class.isAssignableFrom(methodInfo.getReturnType())) {
                return hyperResource.hasLink(linkRelationship, linkName);
            }

            //single hyper link
            if (HyperLink.class.isAssignableFrom(methodInfo.getReturnType())) {
                return ReflectUtils.cast(HyperResource.class, proxy).getLink(linkRelationship, linkName);
            }

            //hyper links
            if (HyperLink[].class.isAssignableFrom(methodInfo.getReturnType())) {
                return ReflectUtils.cast(HyperResource.class, proxy).getLinks(linkRelationship, linkName);
            }

            //If we can get it locally...do it!
            if (hyperResource.canResolveLinkLocal(linkRelationship)) {
                //TODO: when we get to strategies this condition can be much more interesting
                //for now we do this if the return type is an array or the link is a multi link
                if(hyperResource.isMultiLink(linkRelationship) || methodInfo.getReturnType().isArray()) {
                    return this.processEmbeddedResources(methodInfo.getReturnType(), methodInfo.getGenericReturnType(), hyperResource.resolveLinksLocal(linkRelationship));
                } else {
                    return this.processEmbeddedResource(methodInfo.getReturnType(), methodInfo.getGenericReturnType(), hyperResource.resolveLinkLocal(linkRelationship));
                }

            }

            //@Link annotated methods can refer to multiple links...we don't currently support multiple requests
            if (hyperResource.isMultiLink(linkRelationship)) {
               throw new UnsupportedOperationException(MESSAGE_EXCEPTION_SOLVING_MULTI_LINK);
               // return hyperResource.getLinks(linkRelationship);
            }

            //At this point we know it's a single link that's not embedded
            RequestBuilder requestBuilder = hyperResource.getLink(linkRelationship, linkName).toRequestBuilder();
            // Set method type (default=GET)
            requestBuilder.setMethod(link.methodType());
            assignAnnotatedValues(requestBuilder, methodInfo.getParameterAnnotations(), args);
            //If follow support parameters..we could just pass those and call hyperlink.follow(params)..if we could figure out the TypeRef thing...
            return requestProcessor.processRequest(methodInfo.getReturnType(), requestBuilder, typeInfo.make(methodInfo.getGenericReturnType()));
        }

        throw new HyperClientException(Messages.MSG_ERROR_PROXY_CANNOT_HANDLE_METHOD_INVOCATION, method, proxy, args);
    }



    /**
     * Assigns annotated values from request method into the requestBuilder
     *
     * @param requestBuilder  builder for the request
     * @param annotationsPerParams Param annotations
     * @param methodCallParams     parameters values
     */
    protected void assignAnnotatedValues(Request.RequestBuilder requestBuilder, Annotation[][] annotationsPerParams, Object[] methodCallParams) {
        if (methodCallParams != null) {
            for (int i = 0; i < methodCallParams.length; i++) {
                if (methodCallParams[i] != null) {
                    assignAnnotatedValues(requestBuilder, annotationsPerParams[i], methodCallParams[i].toString());
                }
            }
        }
    }

    /**
     * Assigns annotated values into requestBuilder according to annotation types
     *
     * @param requestBuilder  builder for the request
     * @param annotationsPerParams Param annotations
     * @param value                param value
     */
    protected void assignAnnotatedValues(Request.RequestBuilder requestBuilder, Annotation[] annotationsPerParams, String value) {
        for (Annotation annotation : annotationsPerParams) {
            if (Link.Param.class.isInstance(annotation)) {
                requestBuilder.setUrlParam(ReflectUtils.cast(Link.Param.class, annotation).value(), value);
            }

            if (Link.Header.class.isInstance(annotation)) {
                requestBuilder.addHeader(ReflectUtils.cast(Link.Header.class, annotation).value(), value);
            }

            if (Link.Content.class.isInstance(annotation)) {
                requestBuilder.setContentType(ReflectUtils.cast(Link.Content.class, annotation).type());
                requestBuilder.setContentBody(value);
            }
        }
    }

    /**
     * Calculates equals in case of equals method invocation through the proxy
     *
     * @param args equals argument. Should be only one element in the array (object to compare)
     * @return equals comparison result
     */
    protected boolean determineEquals(Object[] args) {
        HyperResourceInvokeHandler otherProxy;

        try {
            otherProxy = (HyperResourceInvokeHandler) Proxy.getInvocationHandler(args[0]);
        } catch (IllegalArgumentException e) {
            return false;
        } catch (ClassCastException e) {
            return false;
        }

        return hyperResource.equals(otherProxy.hyperResource);
    }


}
