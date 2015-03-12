package org.hyperfit.resource.hal.json;


import org.hyperfit.net.BoringRequestBuilder;
import org.hyperfit.net.RFC6570RequestBuilder;
import org.hyperfit.net.RequestBuilder;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;


public class HalLinkTest {

    @Test
    public void testToBuilderTemplated(){
        String href = UUID.randomUUID().toString();

        HalHyperLink link = new HalHyperLink(href, null, true, null, null, null, null, null, null);

        RequestBuilder b = link.toRequestBuilder();

        assertTrue(b instanceof RFC6570RequestBuilder);
    }

    @Test
    public void testToBuilderNotTemplated(){
        String href = UUID.randomUUID().toString();

        HalHyperLink link = new HalHyperLink(href, null, false, null, null, null, null, null, null);

        RequestBuilder b = link.toRequestBuilder();

        assertTrue(b instanceof BoringRequestBuilder);

    }
}
