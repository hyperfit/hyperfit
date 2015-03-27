package org.hyperfit;

import facets.RootResource;
import org.hyperfit.content.ContentType;
import org.hyperfit.net.HyperClient;
import org.hyperfit.net.Request;
import org.hyperfit.net.Response;
import org.hyperfit.content.ContentTypeHandler;
import org.hyperfit.resource.HyperLink;
import org.hyperfit.resource.HyperResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hyperfit.Helpers.*;

public class HyperfitProcessorBuilderTest {

    @Mock
    protected HyperClient mockHyperClient;

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
        HyperfitProcessor requestProcessor =
                HyperfitProcessor.builder()
                .hyperClient(mockHyperClient)
                .build();
        String foo = null;
        requestProcessor.processRequest(RootResource.class, foo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildingEmptyEndpoint(){
        HyperfitProcessor requestProcessor =
                HyperfitProcessor.builder()
                        .hyperClient(mockHyperClient)
                        .build();
        requestProcessor.processRequest(RootResource.class, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildingNullClass(){
        HyperfitProcessor requestProcessor = HyperfitProcessor.builder()
                .hyperClient(mockHyperClient)
                .build();

        requestProcessor.processRequest(null, "http://host.com");
    }
}