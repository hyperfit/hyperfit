package org.hyperfit.content.hal.json;

import org.hyperfit.content.ContentType;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.net.Request;
import org.hyperfit.net.Response;
import org.hyperfit.resource.hal.json.HalJsonResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class HalJsonContentTypeHandlerTest {

    private HalJsonContentTypeHandler halJsonContentTypeHandler;

    @Mock
    private Response mockResponse;

    @Mock
    private Request mockRequest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        halJsonContentTypeHandler = new HalJsonContentTypeHandler();

        when(mockResponse.getRequest())
            .thenReturn(mockRequest);
    }

    @Test
    public void testGetDefaultHandledMediaType() {
        assertEquals(new ContentType("application", "hal+json"), halJsonContentTypeHandler.getDefaultContentType());
    }

    @Test
    public void testGetDefaultHandledMediaTypeNotNull() {
        assertNotNull(halJsonContentTypeHandler.getDefaultContentType());
    }

    @Test
    public void testHandleHyperResponse() throws IOException {
        String validHalJson = "{\"_links\":{\"self\":{\"href\":\"xxx\"}},\"_embedded\":{\"array\":[{\"_links\":{\"self\":{\"href\":\"yyy\"}},\"_embedded\":{\"array\":[{\"state\":\"yyy\"}]},\"state\":\"yyy\"}]},\"state\":\"xxx\"}";

        when(mockResponse.getBody()).thenReturn(validHalJson);
        assertEquals(
                halJsonContentTypeHandler.parseResponse(mockResponse),
                new HalJsonResource(new ObjectMapper().reader(JsonNode.class).readTree(validHalJson)));
    }

    @Test(expected = HyperfitException.class)
    public void testHandleHyperResponseNullBody() {
        when(mockResponse.getBody()).thenReturn(null);
        halJsonContentTypeHandler.parseResponse(mockResponse);
    }

    @Test(expected = HyperfitException.class)
    public void testHandleHyperResponseEmptyBody() {
        when(mockResponse.getBody()).thenReturn("");
        halJsonContentTypeHandler.parseResponse(mockResponse);
    }

    @Test(expected = HyperfitException.class)
    public void testHandleHyperResponseWrongBody() {
        when(mockResponse.getBody()).thenReturn("{");
        halJsonContentTypeHandler.parseResponse(mockResponse);
    }


    @Test
    public void testCapabilities(){
        assertTrue(halJsonContentTypeHandler.canParseResponse());
        assertFalse(halJsonContentTypeHandler.canPrepareRequest());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPrepareRequest(){
        halJsonContentTypeHandler.prepareRequest(null, null);
    }

}
