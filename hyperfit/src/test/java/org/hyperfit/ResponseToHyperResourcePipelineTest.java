package org.hyperfit;

import org.hyperfit.content.ContentRegistry;
import org.hyperfit.content.ContentType;
import org.hyperfit.content.ContentTypeHandler;
import org.hyperfit.errorhandler.ErrorHandler;
import org.hyperfit.exception.ResponseException;
import org.hyperfit.net.*;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.InterfaceSelectionStrategy;
import org.hyperfit.utils.TypeRef;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static test.TestUtils.uniqueString;

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
            Resource1.class
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


        assertSame(
            fakeResource,
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
            Resource1.class
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


        assertSame(
            fakeResource,
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
            Resource1.class
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


        assertSame(
            fakeResource,
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
            Resource1.class
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
        ))
            .thenReturn(
                false
            );



        assertSame(
            fakeResource,
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
            Resource1.class
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

        assertSame(
            fakeResource,
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
            Resource1.class
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

        assertSame(
            fakeResource2,
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
            Resource1.class
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



        when(mockContentTypeHandler.parseResponse(
            mockResponse
        ))
            .thenReturn(fakeResource);

        when(mockResponse.isOK())
            .thenReturn(true);


        assertSame(
            fakeResource,
            subject.run(
                mockResponse
            )
        );

    }


}
