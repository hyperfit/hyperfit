package org.hyperfit;

import facets.RootResource;
import org.hyperfit.content.ContentType;
import org.hyperfit.exception.NoClientRegisteredForSchemeException;
import org.hyperfit.net.*;
import org.hyperfit.content.ContentTypeHandler;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.controls.link.HyperLink;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Null;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hyperfit.Helpers.*;

public class HyperfitProcessorBuilderTest {

    @Mock
    protected HyperClient mockHyperClient;

    @Mock
    protected HyperClient secondMockHyperClient;

    @Mock
    protected Response mockResponse;

    @Mock
    protected ContentTypeHandler mockContentTypeHandler;

    @Mock
    protected HyperResource mockHyperResource;


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBuildAResource(){
        HyperfitProcessor requestProcessor;
        when(mockHyperClient.getSchemes()).thenReturn(new String[]{"http", "https"});
        requestProcessor = HyperfitProcessor.builder()
                .hyperClient(mockHyperClient)
                .build();


        String fakeContentTypeString = "application/hal+json";
        ContentType fakeContentType = ContentType.parse(fakeContentTypeString);

        when(mockHyperClient.execute(isA(Request.class)))
            .thenReturn(this.mockResponse);

        when(mockResponse.getContentType())
            .thenReturn(fakeContentTypeString);

        when(mockResponse.isOK())
            .thenReturn(true);

        when(mockContentTypeHandler.canParseResponse())
        .thenReturn(true);

        when(mockContentTypeHandler.getDefaultContentType())
            .thenReturn(fakeContentType);

        when(mockContentTypeHandler.parseResponse(this.mockResponse))
            .thenReturn(this.mockHyperResource);

        requestProcessor = HyperfitProcessor.builder()
                .hyperClient(mockHyperClient)
                .addContentTypeHandler(mockContentTypeHandler)
                .build();

        String url = "http://example.com";
        RootResource result = requestProcessor.processRequest(RootResource.class, url);


        //Need to verify the proxied result is wrapping the mocked underlying resource
        String fakeRel = UUID.randomUUID().toString();
        HyperLink fakeLink = makeLink(fakeRel);
        when(mockHyperResource.getLink(fakeRel))
            .thenReturn(fakeLink);

        assertEquals(fakeLink, result.getLink(fakeRel));


    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildingNullEndpoint(){
        when(mockHyperClient.getSchemes()).thenReturn(new String[]{"http", "https"});
        HyperfitProcessor requestProcessor =
                HyperfitProcessor.builder()
                .hyperClient(mockHyperClient)
                .build();
        String foo = null;
        requestProcessor.processRequest(RootResource.class, foo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildingEmptyEndpoint(){
        when(mockHyperClient.getSchemes()).thenReturn(new String[]{"http", "https"});
        HyperfitProcessor requestProcessor =
                HyperfitProcessor.builder()
                        .hyperClient(mockHyperClient)
                        .build();
        requestProcessor.processRequest(RootResource.class, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildingNullClass(){
        when(mockHyperClient.getSchemes()).thenReturn(new String[]{"http", "https"});
        HyperfitProcessor requestProcessor = HyperfitProcessor.builder()
                .hyperClient(mockHyperClient)
                .build();

        requestProcessor.processRequest(null, "http://host.com");
    }

    @Test(expected = NoClientRegisteredForSchemeException.class)
    public void testNoClientRegisteredException(){
        when(mockHyperClient.getSchemes()).thenReturn(new String[]{"http", "https"});
        HyperfitProcessor requestProcessor = HyperfitProcessor.builder()
                .hyperClient(mockHyperClient)
                .build();

        requestProcessor.processRequest(RootResource.class, "bbcom://host.com");
    }

    @Test
    public void testUserDefinedHyperClient(){
        when(secondMockHyperClient.getSchemes()).thenReturn(new String[]{"http", "https"});

        String fakeContentTypeString = "application/hal+json";
        ContentType fakeContentType = ContentType.parse(fakeContentTypeString);

        when(secondMockHyperClient.execute(isA(Request.class)))
                .thenReturn(this.mockResponse);

        when(mockHyperClient.execute(isA(Request.class)))
                .thenReturn(this.mockResponse);

        when(mockResponse.getContentType())
                .thenReturn(fakeContentTypeString);

        when(mockResponse.isOK())
                .thenReturn(true);

        when(mockContentTypeHandler.canParseResponse())
                .thenReturn(true);

        when(mockContentTypeHandler.getDefaultContentType())
                .thenReturn(fakeContentType);

        when(mockContentTypeHandler.parseResponse(this.mockResponse))
                .thenReturn(this.mockHyperResource);

        HyperfitProcessor requestProcessor = HyperfitProcessor.builder()
                .hyperClient(secondMockHyperClient)
                .hyperClient(mockHyperClient, "bbcom", "bbcoms")
                .addContentTypeHandler(mockContentTypeHandler)
                .build();
        String endpointUrl ="bbcom://host.com" ;
        requestProcessor.processRequest(RootResource.class, endpointUrl );
        RequestBuilder requestBuilder = BoringRequestBuilder.get(endpointUrl);
        Request request = requestBuilder.build();
        verify(mockHyperClient).execute(request);

        endpointUrl ="bbcoms://host.com" ;
        requestProcessor.processRequest(RootResource.class, endpointUrl);
        requestBuilder = BoringRequestBuilder.get(endpointUrl);
        request = requestBuilder.build();
        verify(mockHyperClient).execute(request);

        endpointUrl ="http://host.com" ;
        requestProcessor.processRequest(RootResource.class, endpointUrl);
        requestBuilder = BoringRequestBuilder.get(endpointUrl);
        request = requestBuilder.build();
        verify(secondMockHyperClient).execute(request);

        endpointUrl ="https://host.com";
        requestProcessor.processRequest(RootResource.class, endpointUrl);
        requestBuilder = BoringRequestBuilder.get(endpointUrl);
        request = requestBuilder.build();
        verify(secondMockHyperClient).execute(request);
    }

    @Test
    public void testGetSchemesNotCalledWhenOverride(){
        when(secondMockHyperClient.getSchemes()).thenReturn(new String[]{"random1", "random2"});
        String fakeContentTypeString = "application/hal+json";
        ContentType fakeContentType = ContentType.parse(fakeContentTypeString);

        when(mockHyperClient.execute(isA(Request.class)))
                .thenReturn(this.mockResponse);

        when(mockResponse.getContentType())
                .thenReturn(fakeContentTypeString);

        when(mockResponse.isOK())
                .thenReturn(true);

        when(mockContentTypeHandler.canParseResponse())
                .thenReturn(true);

        when(mockContentTypeHandler.getDefaultContentType())
                .thenReturn(fakeContentType);

        when(mockContentTypeHandler.parseResponse(this.mockResponse))
                .thenReturn(this.mockHyperResource);


        HyperfitProcessor requestProcessor = HyperfitProcessor.builder()
                .hyperClient(mockHyperClient, "scheme1", "scheme2")
                .addContentTypeHandler(mockContentTypeHandler)
                .build();

        String endpointUrl ="scheme1://host.com" ;
        requestProcessor.processRequest(RootResource.class, endpointUrl );
        RequestBuilder requestBuilder = BoringRequestBuilder.get(endpointUrl);
        Request request = requestBuilder.build();

        verify(mockHyperClient).execute(request);
        verify(mockHyperClient,never()).getSchemes();

        endpointUrl ="scheme2://host.com" ;
        requestProcessor.processRequest(RootResource.class, endpointUrl );
        requestBuilder = BoringRequestBuilder.get(endpointUrl);
        request = requestBuilder.build();

        verify(mockHyperClient).execute(request);
        verify(mockHyperClient,never()).getSchemes();
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionWhenPassEmptySchemeWhenDefineHyperClient(){
        HyperfitProcessor requestProcessor = HyperfitProcessor.builder()
                .hyperClient(mockHyperClient, "", null)
                .addContentTypeHandler(mockContentTypeHandler)
                .build();

        requestProcessor.processRequest(RootResource.class, "scheme1://host.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullHyperClientInBuilder(){
        HyperfitProcessor requestProcessor = HyperfitProcessor.builder()
                .hyperClient(null)
                .addContentTypeHandler(mockContentTypeHandler)
                .build();

        requestProcessor.processRequest(RootResource.class, "scheme1://host.com");
    }

    @Test(expected = NoClientRegisteredForSchemeException.class)
    public void testAtLeastOneClientRegistered(){
        HyperfitProcessor requestProcessor = HyperfitProcessor.builder()
                .addContentTypeHandler(mockContentTypeHandler)
                .build();

        requestProcessor.processRequest(RootResource.class, "scheme1://host.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionWhenUrlNotRight(){
        when(mockHyperClient.getSchemes()).thenReturn(new String[]{"random1", "random2"});
        HyperfitProcessor requestProcessor = HyperfitProcessor.builder()
                .hyperClient(mockHyperClient)
                .addContentTypeHandler(mockContentTypeHandler)
                .build();

        requestProcessor.processRequest(RootResource.class, "host.com");
    }
}
