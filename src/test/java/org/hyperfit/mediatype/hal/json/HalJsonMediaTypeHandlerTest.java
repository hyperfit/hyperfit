package org.hyperfit.mediatype.hal.json;

import org.hyperfit.exception.HyperClientException;
import org.hyperfit.http.Response;
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


public class HalJsonMediaTypeHandlerTest {

    private HalJsonMediaTypeHandler halJsonMediaTypeHandler;

    @Mock
    private Response responseMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        halJsonMediaTypeHandler = new HalJsonMediaTypeHandler();
    }

    @Test
    public void testGetDefaultHandledMediaType() {
        assertEquals(halJsonMediaTypeHandler.getDefaultHandledMediaType(), "application/hal+json");
    }

    @Test
    public void testGetDefaultHandledMediaTypeNotNull() {
        assertNotNull(halJsonMediaTypeHandler.getDefaultHandledMediaType());
    }

    @Test
    public void testHandleHyperResponse() throws IOException {
        String validHalJson = "{\"_links\":{\"self\":{\"href\":\"xxx\"}},\"_embedded\":{\"array\":[{\"_links\":{\"self\":{\"href\":\"yyy\"}},\"_embedded\":{\"array\":[{\"state\":\"yyy\"}]},\"state\":\"yyy\"}]},\"state\":\"xxx\"}";

        when(responseMock.getBody()).thenReturn(validHalJson);
        assertEquals(
                halJsonMediaTypeHandler.handleHyperResponse(responseMock),
                new HalJsonResource(new ObjectMapper().reader(JsonNode.class).readTree(validHalJson)));
    }

    @Test(expected = HyperClientException.class)
    public void testHandleHyperResponseNullBody() {
        when(responseMock.getBody()).thenReturn(null);
        halJsonMediaTypeHandler.handleHyperResponse(responseMock);
    }

    @Test(expected = HyperClientException.class)
    public void testHandleHyperResponseEmptyBody() {
        when(responseMock.getBody()).thenReturn("");
        halJsonMediaTypeHandler.handleHyperResponse(responseMock);
    }

    @Test(expected = HyperClientException.class)
    public void testHandleHyperResponseWrongBody() {
        when(responseMock.getBody()).thenReturn("{");
        halJsonMediaTypeHandler.handleHyperResponse(responseMock);
    }

    @Test(expected = NullPointerException.class)
    public void testHandleHyperResponseNullResponse() {
        halJsonMediaTypeHandler.handleHyperResponse(null);
    }

}
