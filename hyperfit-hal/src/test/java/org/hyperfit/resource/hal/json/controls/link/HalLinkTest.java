package org.hyperfit.resource.hal.json.controls.link;


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

        HalHyperLink link = new HalHyperLink(href, null, true, null, null, null, null, null, null, null);

        RequestBuilder b = link.toRequestBuilder();

        assertTrue(b instanceof RFC6570RequestBuilder);
    }

    @Test
    public void testToBuilderNotTemplated(){
        String href = UUID.randomUUID().toString();

        HalHyperLink link = new HalHyperLink(href, null, false, null, null, null, null, null, null, null);

        RequestBuilder b = link.toRequestBuilder();

        assertTrue(b instanceof BoringRequestBuilder);

    }


    @Test
    public void testResolveRelativeNullBaseURI(){
        String href = "/" + UUID.randomUUID().toString() + "{?params}";

        HalHyperLink link = new HalHyperLink(href, null, false, null, null, null, null, null, null, null);

        assertEquals(href, link.getHref());

    }


    @Test
    public void testResolveRelativeBaseURI(){
        String href = "/" + UUID.randomUUID().toString() + "{?params}";
        String base = "proto://host.tld:984/some/path?params=234";

        HalHyperLink link = new HalHyperLink(href, null, false, null, null, null, null, null, null, base);

        assertEquals("proto://host.tld:984" + href, link.getHref());

    }


    @Test
    public void testResolveAbsoluteBaseURI(){
        String href = "otherproto://somehost.tld:834/" + UUID.randomUUID().toString() + "{?params}";
        String base = "proto://host.tld:984/some/path?params=234";

        HalHyperLink link = new HalHyperLink(href, null, false, null, null, null, null, null, null, base);

        assertEquals(href, link.getHref());

    }
}
