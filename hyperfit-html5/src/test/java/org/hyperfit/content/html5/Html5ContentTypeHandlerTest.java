package org.hyperfit.content.html5;

import org.hyperfit.content.ContentType;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.net.Response;
import org.hyperfit.resource.html5.Html5Resource;
import org.jsoup.Jsoup;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class Html5ContentTypeHandlerTest {

    private Html5ContentTypeHandler html5ContentTypeHandler;

    @Mock
    private Response responseMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        html5ContentTypeHandler = new Html5ContentTypeHandler();
    }

    @Test
    public void testGetDefaultHandledMediaType() {
        assertEquals(new ContentType("application", "xhtml+xml"), html5ContentTypeHandler.getDefaultContentType());
    }

    @Test
    public void testGetDefaultHandledMediaTypeNotNull() {
        assertNotNull(html5ContentTypeHandler.getDefaultContentType());
    }

    @Test
    public void testHandleHyperResponse() throws IOException {
        String validHtml = "<html><head></head><body></body></html>";

        when(responseMock.getBody()).thenReturn(validHtml);
        assertEquals(
            html5ContentTypeHandler.parseResponse(responseMock),
            new Html5Resource(Jsoup.parse(validHtml))
        );
    }

    @Test(expected = HyperfitException.class)
    public void testHandleHyperResponseNullBody() {
        when(responseMock.getBody()).thenReturn(null);
        html5ContentTypeHandler.parseResponse(responseMock);
    }

    @Test(expected = HyperfitException.class)
    public void testHandleHyperResponseEmptyBody() {
        when(responseMock.getBody()).thenReturn("");
        html5ContentTypeHandler.parseResponse(responseMock);
    }





}
