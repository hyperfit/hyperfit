package org.hyperfit.resource.hal.json;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.net.BoringRequestBuilder;
import org.hyperfit.net.RFC6570RequestBuilder;
import org.hyperfit.net.RequestBuilder;
import org.hyperfit.resource.HyperLink;
import org.hyperfit.resource.HyperResource;
import org.hyperfit.resource.HyperResourceException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.hyperfit.TestHelpers.makeSet;
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
