package org.hyperfit.resource.html5.controls.link;


import org.hyperfit.net.BoringRequestBuilder;
import org.hyperfit.net.RFC6570RequestBuilder;
import org.hyperfit.net.RequestBuilder;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertTrue;


public class Html5HyperLinkTest {

    @Test
    public void testToBuilderTemplated(){
        String href = UUID.randomUUID().toString();

        Html5HyperLink link = new Html5HyperLink(href, null, true, null, null, null, null, null, null);

        RequestBuilder b = link.toRequestBuilder();

        assertTrue(b instanceof BoringRequestBuilder);
    }

    @Test
    public void testToBuilderNotTemplated(){
        String href = UUID.randomUUID().toString();

        Html5HyperLink link = new Html5HyperLink(href, null, false, null, null, null, null, null, null);

        RequestBuilder b = link.toRequestBuilder();

        assertTrue(b instanceof BoringRequestBuilder);

    }
}
