package org.hyperfit;

import facets.RootResource;
import org.hyperfit.net.HyperClient;
import org.hyperfit.net.Request;
import org.hyperfit.net.Response;
import org.hyperfit.mediatype.MediaTypeHandler;
import org.hyperfit.resource.HyperLink;
import org.hyperfit.resource.HyperResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hyperfit.TestHelpers.*;

public class RootResourceBuilderTest {

    @Mock
    protected HyperClient mockHyperClient;

    @Mock
    protected Response mockResponse;

    @Mock
    protected MediaTypeHandler mockMediaTypeHandler;

    @Mock
    protected HyperResource mockHyperResource;


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBuildAResource(){
        RootResourceBuilder builder = new RootResourceBuilder();

        String fakeContentType = "application/hal+json";

        when(mockHyperClient.execute(isA(Request.class)))
            .thenReturn(this.mockResponse);

        when(mockResponse.getContentType())
            .thenReturn(fakeContentType);

        when(mockResponse.isOK())
            .thenReturn(true);

        when(mockMediaTypeHandler.getDefaultHandledMediaType())
            .thenReturn(fakeContentType);

        when(mockMediaTypeHandler.parseHyperResponse(this.mockResponse))
            .thenReturn(this.mockHyperResource);

        builder.hyperClient(this.mockHyperClient)
            .addMediaTypeHandler(mockMediaTypeHandler);

        String url = "http://example.com";
        RootResource result = builder.build(RootResource.class, url);


        //Need to verify the proxied result is wrapping the mocked underlying resource
        String fakeRel = UUID.randomUUID().toString();
        HyperLink fakeLink = makeLink(fakeRel);
        when(mockHyperResource.getLink(fakeRel))
            .thenReturn(fakeLink);

        assertEquals(fakeLink, result.getLink(fakeRel));


    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildingNullEndpoint(){
        RootResourceBuilder builder = new RootResourceBuilder();
        builder.build(RootResource.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildingEmptyEndpoint(){
        RootResourceBuilder builder = new RootResourceBuilder();
        builder.build(RootResource.class, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildingNullClass(){
        RootResourceBuilder builder = new RootResourceBuilder();

        builder.build(null, "http://host.com");
    }
}
