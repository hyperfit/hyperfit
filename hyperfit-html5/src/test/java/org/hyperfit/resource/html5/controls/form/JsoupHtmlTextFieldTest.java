package org.hyperfit.resource.html5.controls.form;


import org.hyperfit.Helpers;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.net.Method;
import org.hyperfit.resource.controls.form.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class JsoupHtmlTextFieldTest {


    Element formElement;
    Element inputElement;
    @Before
    public void setUp(){
        Document doc = Jsoup.parse("<html>" +
        "<head>" +
        "</head>" +
        "" +
        "<body>" +
        "<form>" +
        "<input type=\"text\"/>" +
        "</form>" +
        "</body>" +
        "" +
        "" +
        "</html>");


        formElement = doc.select("form").get(0);
        inputElement = doc.select("input").get(0);
    }


    @Test
    public void testGetName() {

        String name = UUID.randomUUID().toString();
        formElement.attr("name", name);

        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        assertEquals(name, subject.getName());
    }



}
