package org.hyperfit.content.hal.json;

import org.hyperfit.content.ContentType;
import org.hyperfit.content.hal.json.HalJsonContentTypeHandler;
import org.hyperfit.exception.HyperfitException;
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

    private HalJsonContentTypeHandler halJsonMediaTypeHandler;

    @Mock
    private Response responseMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        halJsonMediaTypeHandler = new HalJsonContentTypeHandler();
    }

    @Test
    public void testGetDefaultHandledMediaType() {
        assertEquals(new ContentType("application", "hal+json"), halJsonMediaTypeHandler.getDefaultContentType());
    }

    @Test
    public void testGetDefaultHandledMediaTypeNotNull() {
        assertNotNull(halJsonMediaTypeHandler.getDefaultContentType());
    }

    @Test
    public void testHandleHyperResponse() throws IOException {
        String validHalJson = "{\"_links\":{\"self\":{\"href\":\"xxx\"}},\"_embedded\":{\"array\":[{\"_links\":{\"self\":{\"href\":\"yyy\"}},\"_embedded\":{\"array\":[{\"state\":\"yyy\"}]},\"state\":\"yyy\"}]},\"state\":\"xxx\"}";

        when(responseMock.getBody()).thenReturn(validHalJson);
        assertEquals(
                halJsonMediaTypeHandler.parseResponse(responseMock),
                new HalJsonResource(new ObjectMapper().reader(JsonNode.class).readTree(validHalJson)));
    }

    @Test(expected = HyperfitException.class)
    public void testHandleHyperResponseNullBody() {
        when(responseMock.getBody()).thenReturn(null);
        halJsonMediaTypeHandler.parseResponse(responseMock);
    }

    @Test(expected = HyperfitException.class)
    public void testHandleHyperResponseEmptyBody() {
        when(responseMock.getBody()).thenReturn("");
        halJsonMediaTypeHandler.parseResponse(responseMock);
    }

    @Test(expected = HyperfitException.class)
    public void testHandleHyperResponseWrongBody() {
        when(responseMock.getBody()).thenReturn("{");
        halJsonMediaTypeHandler.parseResponse(responseMock);
    }



}
