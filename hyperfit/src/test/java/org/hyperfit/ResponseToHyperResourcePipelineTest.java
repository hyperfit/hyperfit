package org.hyperfit;

import org.hyperfit.content.ContentRegistry;
import org.hyperfit.content.ContentType;
import org.hyperfit.content.ContentTypeHandler;
import org.hyperfit.errorhandler.ErrorHandler;
import org.hyperfit.net.*;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.utils.TypeInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class ResponseToHyperResourcePipelineTest {

    interface Resource1 extends HyperResource {

    }

    @Mock
    HyperfitProcessor mockHyperfitProcessor;

    @Mock
    ErrorHandler mockErrorHandler;


    @Mock
    ContentRegistry mockContentRegistry;

    @Mock
    ContentTypeHandler mockContentTypeHandler;

    @Mock
    Response mockResponse;

    @Mock
    TypeInfo mockTypeInfo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

    }


    @Test
    public void testRunEmptyContentType() {

        ResponseToHyperResourcePipeline subject = new ResponseToHyperResourcePipeline(
            Collections.<ResponseToHyperResourcePipeline.Step<Response,HyperResource>>emptyList(),
            mockHyperfitProcessor,
            mockContentRegistry,
            mockErrorHandler,
            Resource1.class,
            mockTypeInfo
        );

        HyperResource fakeResource = mock(HyperResource.class);


        when(mockErrorHandler.unhandledContentType(
            mockHyperfitProcessor,
            mockResponse,
            mockContentRegistry,
            Resource1.class
        )).thenReturn(
            fakeResource
        );


        when(mockResponse.getContentType())
            .thenReturn("");


        Resource1 fakeProxiedResource = mock(Resource1.class);

        when(mockHyperfitProcessor.processResource(
            Resource1.class,
            fakeResource,
            mockTypeInfo
        )).thenReturn(
            fakeProxiedResource
        );


        assertSame(
            fakeProxiedResource,
            subject.run(
                mockResponse
            )
        );

        verifyZeroInteractions(mockContentRegistry);

    }


    @Test
    public void testRunNullContentType() {

        ResponseToHyperResourcePipeline subject = new ResponseToHyperResourcePipeline(
            Collections.<ResponseToHyperResourcePipeline.Step<Response,HyperResource>>emptyList(),
            mockHyperfitProcessor,
            mockContentRegistry,
            mockErrorHandler,
            Resource1.class,
            mockTypeInfo
        );

        HyperResource fakeResource = mock(HyperResource.class);


        when(mockErrorHandler.unhandledContentType(
            mockHyperfitProcessor,
            mockResponse,
            mockContentRegistry,
            Resource1.class
        )).thenReturn(
            fakeResource
        );


        when(mockResponse.getContentType())
            .thenReturn(null);

        Resource1 fakeProxiedResource = mock(Resource1.class);

        when(mockHyperfitProcessor.processResource(
            Resource1.class,
            fakeResource,
            mockTypeInfo
        )).thenReturn(
            fakeProxiedResource
        );

        assertSame(
            fakeProxiedResource,
            subject.run(
                mockResponse
            )
        );

        verifyZeroInteractions(mockContentRegistry);

    }


    @Test
    public void testRunParseContentTypeThrows() {

        ResponseToHyperResourcePipeline subject = new ResponseToHyperResourcePipeline(
            Collections.<ResponseToHyperResourcePipeline.Step<Response,HyperResource>>emptyList(),
            mockHyperfitProcessor,
            mockContentRegistry,
            mockErrorHandler,
            Resource1.class,
            mockTypeInfo
        );

        HyperResource fakeResource = mock(HyperResource.class);


        when(mockErrorHandler.unhandledContentType(
            mockHyperfitProcessor,
            mockResponse,
            mockContentRegistry,
            Resource1.class
        )).thenReturn(
            fakeResource
        );


        when(mockResponse.getContentType())
            .thenReturn("three/part/throws");

        Resource1 fakeProxiedResource = mock(Resource1.class);

        when(mockHyperfitProcessor.processResource(
            Resource1.class,
            fakeResource,
            mockTypeInfo
        )).thenReturn(
            fakeProxiedResource
        );


        assertSame(
            fakeProxiedResource,
            subject.run(
                mockResponse
            )
        );

        verifyZeroInteractions(mockContentRegistry);

    }


    @Test
    public void testRunUnhandledContentType() {

        ResponseToHyperResourcePipeline subject = new ResponseToHyperResourcePipeline(
            Collections.<ResponseToHyperResourcePipeline.Step<Response,HyperResource>>emptyList(),
            mockHyperfitProcessor,
            mockContentRegistry,
            mockErrorHandler,
            Resource1.class,
            mockTypeInfo
        );

        HyperResource fakeResource = mock(HyperResource.class);


        when(mockErrorHandler.unhandledContentType(
            mockHyperfitProcessor,
            mockResponse,
            mockContentRegistry,
            Resource1.class
        )).thenReturn(
            fakeResource
        );

        String fakeContentType = "not/real";
        when(mockResponse.getContentType())
            .thenReturn(fakeContentType);

        when(mockContentRegistry.canHandle(
            ContentType.parse(fakeContentType),
            ContentRegistry.Purpose.PARSE_RESPONSE
        )).thenReturn(
            false
        );



        Resource1 fakeProxiedResource = mock(Resource1.class);

        when(mockHyperfitProcessor.processResource(
            Resource1.class,
            fakeResource,
            mockTypeInfo
        )).thenReturn(
            fakeProxiedResource
        );

        assertSame(
            fakeProxiedResource,
            subject.run(
                mockResponse
            )
        );

    }




    @Test
    public void testRunContentParserThrows() {

        ResponseToHyperResourcePipeline subject = new ResponseToHyperResourcePipeline(
            Collections.<ResponseToHyperResourcePipeline.Step<Response,HyperResource>>emptyList(),
            mockHyperfitProcessor,
            mockContentRegistry,
            mockErrorHandler,
            Resource1.class,
            mockTypeInfo
        );

        HyperResource fakeResource = mock(HyperResource.class);




        String fakeContentType = "not/real";
        when(mockResponse.getContentType())
            .thenReturn(fakeContentType);

        when(mockContentRegistry.canHandle(
            ContentType.parse(fakeContentType),
            ContentRegistry.Purpose.PARSE_RESPONSE
        ))
            .thenReturn(
                true
            );


        when(mockContentRegistry.getHandler(
            ContentType.parse(fakeContentType),
            ContentRegistry.Purpose.PARSE_RESPONSE
        ))
            .thenReturn(
                mockContentTypeHandler
            );


        RuntimeException fakeException = new RuntimeException("blah");
        when(mockContentTypeHandler.parseResponse(
            mockResponse
        ))
            .thenThrow(fakeException);


        when(mockErrorHandler.contentParseError(
            mockHyperfitProcessor,
            mockResponse,
            mockContentRegistry,
            Resource1.class,
            fakeException
        )).thenReturn(
            fakeResource
        );


        Resource1 fakeProxiedResource = mock(Resource1.class);

        when(mockHyperfitProcessor.processResource(
            Resource1.class,
            fakeResource,
            mockTypeInfo
        )).thenReturn(
            fakeProxiedResource
        );

        assertSame(
            fakeProxiedResource,
            subject.run(
                mockResponse
            )
        );

    }



    @Test
    public void testRunNotOKResponse() {

        ResponseToHyperResourcePipeline subject = new ResponseToHyperResourcePipeline(
            Collections.<ResponseToHyperResourcePipeline.Step<Response,HyperResource>>emptyList(),
            mockHyperfitProcessor,
            mockContentRegistry,
            mockErrorHandler,
            Resource1.class,
            mockTypeInfo
        );

        HyperResource fakeResource1 = mock(HyperResource.class);
        HyperResource fakeResource2 = mock(HyperResource.class);



        String fakeContentType = "not/real";
        when(mockResponse.getContentType())
            .thenReturn(fakeContentType);

        when(mockContentRegistry.canHandle(
            ContentType.parse(fakeContentType),
            ContentRegistry.Purpose.PARSE_RESPONSE
        ))
            .thenReturn(
                true
            );


        when(mockContentRegistry.getHandler(
            ContentType.parse(fakeContentType),
            ContentRegistry.Purpose.PARSE_RESPONSE
        ))
            .thenReturn(
                mockContentTypeHandler
            );



        when(mockContentTypeHandler.parseResponse(
            mockResponse
        ))
            .thenReturn(fakeResource1);

        when(mockResponse.isOK())
            .thenReturn(false);

        when(mockErrorHandler.notOKResponse(
            mockHyperfitProcessor,
            mockResponse,
            mockContentRegistry,
            Resource1.class,
            fakeResource1
        )).thenReturn(
            fakeResource2
        );

        Resource1 fakeProxiedResource = mock(Resource1.class);

        when(mockHyperfitProcessor.processResource(
            Resource1.class,
            fakeResource2,
            mockTypeInfo
        )).thenReturn(
            fakeProxiedResource
        );


        assertSame(
            fakeProxiedResource,
            subject.run(
                mockResponse
            )
        );

    }


    @Test
    public void testRunHappyPath() {

        ResponseToHyperResourcePipeline subject = new ResponseToHyperResourcePipeline(
            Collections.<ResponseToHyperResourcePipeline.Step<Response,HyperResource>>emptyList(),
            mockHyperfitProcessor,
            mockContentRegistry,
            mockErrorHandler,
            Resource1.class,
            mockTypeInfo
        );

        HyperResource fakeBaseResource = mock(HyperResource.class);
        Resource1 fakeProxyResource = mock(Resource1.class);


        String fakeContentType = "not/real";
        when(mockResponse.getContentType())
            .thenReturn(fakeContentType);

        when(mockContentRegistry.canHandle(
            ContentType.parse(fakeContentType),
            ContentRegistry.Purpose.PARSE_RESPONSE
        )).thenReturn(
            true
        );


        when(mockContentRegistry.getHandler(
            ContentType.parse(fakeContentType),
            ContentRegistry.Purpose.PARSE_RESPONSE
        )).thenReturn(
            mockContentTypeHandler
        );



        when(mockContentTypeHandler.parseResponse(
            mockResponse
        )).thenReturn(
            fakeBaseResource
        );

        when(mockResponse.isOK())
            .thenReturn(true);


        when(mockHyperfitProcessor.processResource(
            Resource1.class,
            fakeBaseResource,
            mockTypeInfo
        )).thenReturn(
            fakeProxyResource
        );


        assertSame(
            fakeProxyResource,
            subject.run(
                mockResponse
            )
        );

    }



    @Test
    public void testRunWithSomeCustomSteps() {

        String fakeContentType = "not/real";
        final Response responseOverriddenInStep1 = mock(Response.class);
        when(responseOverriddenInStep1.getContentType())
            .thenReturn(fakeContentType);

        final HyperResource resultOverriddenInStep2 = mock(HyperResource.class);

        Pipeline.Step<Response,HyperResource> step1 = new Pipeline.Step<Response, HyperResource>(){
            public HyperResource run(
                Response input,
                Pipeline<Response, HyperResource> pipeline
            ) {

                assertSame(
                    "response passed to run in step1 must be the response seen in step 1",
                    mockResponse,
                    input
                );


                HyperResource result = pipeline.run(responseOverriddenInStep1);

                assertSame(
                    "result from pipeline.run must be result from step 2",
                    resultOverriddenInStep2,
                    result
                );


                return result;

            }
        };


        final Resource1 resourceFromResponse = mock(Resource1.class);

        Pipeline.Step<Response,HyperResource> step2 = new Pipeline.Step<Response, HyperResource>(){
            public HyperResource run(
                Response input,
                Pipeline<Response, HyperResource> pipeline
            ) {

                assertSame(
                    "response passed to run in step2 must be the response sent by step1",
                    responseOverriddenInStep1,
                    input
                );


                HyperResource result =  pipeline.run(input);

                assertSame(
                    "result from pipeline.run must be result from response",
                    resourceFromResponse,
                    result
                );

                return resultOverriddenInStep2;
            }
        };



        ResponseToHyperResourcePipeline subject = new ResponseToHyperResourcePipeline(
            //techinaclly order is not guaranteed....not sure what we should do about that right now...
            Arrays.asList(
                step2,
                step1
            ),
            mockHyperfitProcessor,
            mockContentRegistry,
            mockErrorHandler,
            Resource1.class,
            mockTypeInfo
        );



        //This never gets used because it's replaced in step 1
        verifyZeroInteractions(mockResponse);

        when(mockContentRegistry.canHandle(
            ContentType.parse(fakeContentType),
            ContentRegistry.Purpose.PARSE_RESPONSE
        )).thenReturn(
            true
        );


        when(mockContentRegistry.getHandler(
            ContentType.parse(fakeContentType),
            ContentRegistry.Purpose.PARSE_RESPONSE
        )).thenReturn(
            mockContentTypeHandler
        );



        when(mockContentTypeHandler.parseResponse(
            responseOverriddenInStep1
        )).thenReturn(
            resourceFromResponse
        );

        when(mockHyperfitProcessor.processResource(
            Resource1.class,
            resourceFromResponse,
            mockTypeInfo
        )).thenReturn(
            resourceFromResponse
        );

        when(responseOverriddenInStep1.isOK())
            .thenReturn(true);


        assertSame(
            resultOverriddenInStep2,
            subject.run(
                mockResponse
            )
        );

    }

}
