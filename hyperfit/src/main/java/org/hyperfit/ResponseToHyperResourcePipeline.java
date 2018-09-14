package org.hyperfit;

import org.hyperfit.content.ContentRegistry;
import org.hyperfit.content.ContentType;
import org.hyperfit.content.ContentTypeHandler;
import org.hyperfit.errorhandler.ErrorHandler;
import org.hyperfit.net.Response;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.utils.StringUtils;
import org.hyperfit.utils.TypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Stack;


class ResponseToHyperResourcePipeline implements Pipeline<Response, HyperResource>{

    private static final Logger LOG = LoggerFactory.getLogger(ResponseToHyperResourcePipeline.class);

    private final Stack<Step<Response, HyperResource>> steps = new Stack<Step<Response, HyperResource>>();
    private final TypeInfo typeInfo;
    private final ContentRegistry contentRegistry;
    private final ErrorHandler errorHandler;
    private final Class<? extends HyperResource> expectedInterface;
    private final HyperfitProcessor processor;


    ResponseToHyperResourcePipeline(
        List<Step<Response, HyperResource>> steps,
        HyperfitProcessor processor,
        ContentRegistry contentRegistry,
        ErrorHandler errorHandler,
        Class<? extends HyperResource> expectedInterface,
        TypeInfo typeInfo

    ) {

        this.steps.addAll(steps);

        this.typeInfo = typeInfo;
        this.processor = processor;
        this.contentRegistry = contentRegistry;
        this.errorHandler = errorHandler;
        this.expectedInterface = expectedInterface;
    }





    public HyperResource run(
        Response response
    ){
        if(steps.empty()){

            //TODO: should these all just be steps somehow? think taht'd require more context

            //TODO: may be better to make this look at response code first
            //and try to parse if it can undertsand the type
            //possibly as an Optional<HyperResource>?

            //STAGE 1 - There's response, let's see if we understand the content type!
            ContentType responseContentType = null;

            //Sometimes there is no content type, no need to warn about that
            if (!StringUtils.isEmpty(response.getContentType())){
                try {
                    responseContentType = ContentType.parse(response.getContentType());
                } catch (Exception e) {
                    LOG.warn("Error parsing content type of response.  errorHandler:unhandledContentType will be called", e);
                }
            }

            //See if we have a content type, if not throw
            if(responseContentType == null || !this.contentRegistry.canHandle(responseContentType, ContentRegistry.Purpose.PARSE_RESPONSE)){
                //We don't understand the content type, let's ask the error handler what to do!
                return this.errorHandler.unhandledContentType(
                    processor,
                    response,
                    contentRegistry,
                    expectedInterface
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
                    processor,
                    response,
                    contentRegistry,
                    expectedInterface,
                    e
                );
            }


            //STAGE 3 - we were able to parse the response into a HyperResponse, let's make sure it's a OK response
            if(!response.isOK()){
                return this.errorHandler.notOKResponse(
                    processor,
                    response,
                    contentRegistry,
                    expectedInterface,
                    resource
                );
            }


            //Everything with the resource worked out, let's return it
            return processor.processResource(
                expectedInterface,
                resource,
                typeInfo
            );
        }

        return steps.pop().run(response,this);
    }

}
