package org.hyperfit.resource.html5.controls.form;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;


public class JsoupHtmlTextFieldSetTest {


    Element formElement;
    Element fieldSetElement;
    @Before
    public void setUp(){
        Document doc = Jsoup.parse("<html>" +
        "<head>" +
        "</head>" +
        "" +
        "<body>" +
        "<form>" +
        "<fieldset/>" +
        "</form>" +
        "</body>" +
        "" +
        "" +
        "</html>");


        formElement = doc.select("form").get(0);
        fieldSetElement = doc.select("fieldset").get(0);
    }


    @Test
    public void testGetName() {

        String name = UUID.randomUUID().toString();
        fieldSetElement.attr("name", name);

        JsoupHtml5FieldSet subject = new JsoupHtml5FieldSet(fieldSetElement, formElement);

        assertEquals(name, subject.getName());
    }


    @Test
    public void testGetLabel() {

        JsoupHtml5FieldSet subject = new JsoupHtml5FieldSet(fieldSetElement, formElement);

        assertNull(subject.getLabel());

        String label = UUID.randomUUID().toString();
        Element legendElement = fieldSetElement.appendElement("legend");

        legendElement.text(label);

        subject = new JsoupHtml5FieldSet(fieldSetElement, formElement);

        assertEquals(label, subject.getLabel());
    }







}
