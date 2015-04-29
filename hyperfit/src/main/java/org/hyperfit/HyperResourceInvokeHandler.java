package org.hyperfit;

import org.hyperfit.annotation.*;
import org.hyperfit.content.ContentRegistry;
import org.hyperfit.content.ContentType;
import org.hyperfit.content.ContentTypeHandler;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.message.Messages;
import org.hyperfit.methodinfo.MethodInfo;
import org.hyperfit.methodinfo.MethodInfoCache;
import org.hyperfit.net.RequestBuilder;
import org.hyperfit.resource.controls.form.Form;
import org.hyperfit.resource.controls.link.HyperLink;
import org.hyperfit.resource.controls.link.HyperLinkWrapper;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.HyperResourceException;
import org.hyperfit.utils.ReflectUtils;
import org.hyperfit.utils.StringUtils;
import org.hyperfit.utils.TypeInfo;
import org.javatuples.Pair;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.hyperfit.methodinfo.MethodInfo.MethodType;

/**
 * <p>This class performs all the mappings associated to annotated interfaces for resource interfaces</p>
 * <p>Retrieves values for annotated methods with @Data annotation, or follow hyper media links
 * for methods annotated with @Link annotation</p>
 */
public class HyperResourceInvokeHandler implements InvocationHandler {

    private static final String MESSAGE_EXCEPTION_SOLVING_MULTI_LINK = "Cannot resolve a multi link resource";

    private final HyperResource hyperResource;
    private final HyperfitProcessor requestProcessor;

    private final MethodInfoCache methodInfoCache;
    private TypeInfo typeInfo;

    public HyperResourceInvokeHandler(HyperResource hyperResource, HyperfitProcessor requestProcessor, MethodInfoCache methodInfoCache, TypeInfo typeInfo) {
        this.hyperResource = hyperResource;
        this.requestProcessor = requestProcessor;

        this.typeInfo = (typeInfo != null) ? typeInfo : new TypeInfo();

        this.methodInfoCache = methodInfoCache;
    }

    protected HyperLink extendHyperLink(HyperLink hyperLink) {
        return new HyperLinkWrapper(hyperLink) {

            @Override
            public <R> R follow(Class<R> returnClass, Type genericReturnType) {

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
                    return requestProcessor.processResource(returnClass, hyperResource.resolveLinkLocal(linkRelationship), typeInfo.make(genericReturnType));
                }

                RequestBuilder requestBuilder = this.toRequestBuilder();
                return requestProcessor.processRequest(returnClass, requestBuilder, typeInfo.make(genericReturnType));
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
        } catch (HyperfitException hce) {
            throw hce; //don't wrap up hyperfit exceptions
        } catch (Exception e) {
            throw new HyperfitException(e, Messages.MSG_ERROR_PROXY_UNEXPECTED_ERROR, method, proxy, args);
        }
    }


    protected <T> T processEmbeddedResources(Class<T> returnClass, Type genericReturnType, HyperResource[] hyperResources){
        //TODO: in the future this would actually be a strategy...not these if blocks

        if (returnClass.isArray()) {
            Pair<? extends Class<?>,Type> arrayTypeInfo = typeInfo.getArrayType(returnClass, genericReturnType);
            TypeInfo newInfo = typeInfo.make(arrayTypeInfo.getValue1());

            Object[] result = ReflectUtils.createArray(arrayTypeInfo.getValue0(), hyperResources.length);

            for (int i = 0; i < hyperResources.length; i++) {
                result[i] =  this.requestProcessor.processResource(arrayTypeInfo.getValue0(), hyperResources[i], newInfo);
            }

            return (T)result;

        }

        throw new RuntimeException("Can't deal with return type");
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

            //NOTE: this seems a bit hacky, but will a link ever return a boolean? probably not
            if (boolean.class.isAssignableFrom(methodInfo.getReturnType())) {
                return hyperResource.hasLink(linkRelationship);
            }

            //single hyper link
            if (HyperLink.class.isAssignableFrom(methodInfo.getReturnType())) {
                return ReflectUtils.cast(HyperResource.class, proxy).getLink(linkRelationship);
            }

            //hyper links
            if (HyperLink[].class.isAssignableFrom(methodInfo.getReturnType())) {
                return ReflectUtils.cast(HyperResource.class, proxy).getLinks(linkRelationship);
            }

            //If we can get it locally...do it!
            if (hyperResource.canResolveLinkLocal(linkRelationship)) {
                //TODO: when we get to strategies this condition can be much more interesting
                //for now we do this if the return type is an array or the link is a multi link
                if(hyperResource.isMultiLink(linkRelationship) || methodInfo.getReturnType().isArray()) {
                    return this.processEmbeddedResources(methodInfo.getReturnType(), methodInfo.getGenericReturnType(), hyperResource.resolveLinksLocal(linkRelationship));
                } else {
                    return this.requestProcessor.processResource(methodInfo.getReturnType(), hyperResource.resolveLinkLocal(linkRelationship), typeInfo.make(methodInfo.getGenericReturnType()));
                }

            }

            //@Link annotated methods can refer to multiple links...we don't currently support multiple requests
            if (hyperResource.isMultiLink(linkRelationship)) {
               throw new UnsupportedOperationException(MESSAGE_EXCEPTION_SOLVING_MULTI_LINK);
            }

            //At this point we know it's a single link that's not embedded
            RequestBuilder requestBuilder = hyperResource.getLink(linkRelationship).toRequestBuilder();
            // Set method type (default=GET)
            requestBuilder.setMethod(methodInfo.getRequestMethod());
            assignAnnotatedValues(requestBuilder, methodInfo.getParameterAnnotations(), args);
            //If follow supported parameters..we could just pass those and call hyperlink.follow(params)..if we could figure out the TypeRef thing...
            return requestProcessor.processRequest(methodInfo.getReturnType(), requestBuilder, typeInfo.make(methodInfo.getGenericReturnType()));
        }


        // Named Link method invocation
        //TODO: this is VERY similar to above, it just calls different methods
        //maybe when we move to strategies this will get cleaned up
        NamedLink namedLink = methodInfo.getNamedLinkAnnotation();
        if (namedLink != null) {
            String linkRelationship = namedLink.rel();
            String linkName = namedLink.name();

            //java doesn't let us have nulls..but we can have null names on links so we do this
            if(linkName.equals(NamedLink.NULL)){
                linkName = null;
            }

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

            //TODO: we have no concept of resolving a named link locally.  We would need a canResolveLocal(rel, name) and resolveLocal(rel, name)
            //HAL has no way to identify a named embedded link so for now we don't even bother checking for that..


            //At this point we know it's a single link that's not embedded
            RequestBuilder requestBuilder = hyperResource.getLink(linkRelationship, linkName).toRequestBuilder();
            // Set method type (default=GET)
            requestBuilder.setMethod(methodInfo.getRequestMethod());
            assignAnnotatedValues(requestBuilder, methodInfo.getParameterAnnotations(), args);
            //If follow supported parameters..we could just pass those and call hyperlink.follow(params)..if we could figure out the TypeRef thing...
            return requestProcessor.processRequest(methodInfo.getReturnType(), requestBuilder, typeInfo.make(methodInfo.getGenericReturnType()));
        }


        // First Link method invocation
        //TODO: first link annotated methods don't currently perform all the functions of other link annotated methods
        //TODO: this is built to be a custom strategy on top of basic Resource functions
        FirstLink firstLink = methodInfo.getFirstLinkAnnotation();
        if (firstLink != null) {
            if (HyperLink.class.isAssignableFrom(methodInfo.getReturnType())) {

                String relationship = firstLink.rel();
                HyperLink[] relLinks = hyperResource.getLinks(relationship);

                if(relLinks.length == 0){
                    throw new HyperResourceException(Messages.MSG_ERROR_LINK_WITH_REL_NOT_FOUND, relationship);
                }

                for(String name : firstLink.names()){
                    //java doesn't let us have nulls..but we can have null names on links so we do this
                    if(name.equals(FirstLink.NULL)){
                        name = null;
                    }

                    if(StringUtils.equals(name, FirstLink.MATCH_ANY_NAME)){
                        //If it's the wildcard, just return the first one
                        return ReflectUtils.cast(HyperResource.class, proxy).getLink(relationship, relLinks[0].getName());
                    }

                    for(HyperLink relLink : relLinks){
                        if(StringUtils.equals(name, relLink.getName())){
                            return ReflectUtils.cast(HyperResource.class, proxy).getLink(relationship, relLink.getName());
                        }
                    }
                }

                //If it was never found indicate that.
                throw new HyperResourceException(Messages.MSG_ERROR_LINK_WITH_NAME_NOT_FOUND, relationship, Arrays.toString(firstLink.names()));

            }

        }

        NamedForm namedFormAnnotation = methodInfo.getNamedFormAnnotation();
        if (namedFormAnnotation != null) {
            if (boolean.class.isAssignableFrom(methodInfo.getReturnType())){
                return hyperResource.hasForm(namedFormAnnotation.value());
            }

            if(Form.class.isAssignableFrom(methodInfo.getReturnType())){
                return hyperResource.getForm(namedFormAnnotation.value());
            }
        }

        throw new HyperfitException(Messages.MSG_ERROR_PROXY_CANNOT_HANDLE_METHOD_INVOCATION, method, proxy, args);
    }



    /**
     * Assigns annotated values from request method into the requestBuilder
     *
     * @param requestBuilder  builder for the request
     * @param annotationsPerParams Param annotations
     * @param methodCallParams     parameters values
     */
    protected void assignAnnotatedValues(org.hyperfit.net.RequestBuilder requestBuilder, Annotation[][] annotationsPerParams, Object[] methodCallParams) {
        if (methodCallParams != null) {
            for (int i = 0; i < methodCallParams.length; i++) {
                if (methodCallParams[i] != null) {
                    assignAnnotatedValues(requestBuilder, annotationsPerParams[i], methodCallParams[i]);
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
    protected void assignAnnotatedValues(org.hyperfit.net.RequestBuilder requestBuilder, Annotation[] annotationsPerParams, Object value) {
        for (Annotation annotation : annotationsPerParams) {
            if (Param.class.isInstance(annotation)) {
                requestBuilder.setParam(ReflectUtils.cast(Param.class, annotation).value(), value);
            }

            if (Header.class.isInstance(annotation)) {
                requestBuilder.addHeader(ReflectUtils.cast(Header.class, annotation).value(), value.toString());
            }

            if(Content.class.isInstance(annotation)){
                //this seems dangerous...should we force a default value?
                ContentType contentType = ContentType.parse(Content.class.cast(annotation).value());

                //TODO: make this protected hack non-sense go away...something is wrong with our class layout if we need to do this
                ContentTypeHandler contentEncoder = this.requestProcessor.contentRegistry.getHandler(contentType, ContentRegistry.Purpose.PREPARE_REQUEST);
                contentEncoder.prepareRequest(requestBuilder, value);
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
