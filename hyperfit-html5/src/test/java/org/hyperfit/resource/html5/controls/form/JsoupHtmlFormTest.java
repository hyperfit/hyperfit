package org.hyperfit.resource.html5.controls.form;


import org.hyperfit.Helpers;
import org.hyperfit.net.BoringRequestBuilder;
import org.hyperfit.net.Method;
import org.hyperfit.net.RequestBuilder;
import org.hyperfit.resource.html5.controls.link.Html5HyperLink;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class JsoupHtmlFormTest {

    @Test
    public void testGetName() {
        Element formElement = Jsoup.parse(
            "<form/>"
        );

        String name = UUID.randomUUID().toString();
        formElement.attr("name", name);

        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        assertEquals(name, subject.getName());
    }


    @Test
    public void testGetHref() {
        Element formElement = Jsoup.parse(
            "<form/>"
        );

        String action = UUID.randomUUID().toString();
        formElement.attr("action", action);

        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        assertEquals(action, subject.getHref());
    }

    @Test
    public void testGetMethod() {
        Element formElement = Jsoup.parse(
            "<form/>"
        );

        String method = Helpers.random("get", "GET", "POST", "post");
        formElement.attr("method", method);

        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        assertEquals(Method.valueOf(method.toUpperCase()), subject.getMethod());
    }

    @Test
    public void testGetMethodDefaultsToGET() {
        Element formElement = Jsoup.parse(
            "<form/>"
        );

        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        assertEquals(Method.GET, subject.getMethod());
    }
}
