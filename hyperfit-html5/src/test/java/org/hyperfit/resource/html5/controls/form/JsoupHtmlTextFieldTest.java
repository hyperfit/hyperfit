package org.hyperfit.resource.html5.controls.form;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;


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
        inputElement.attr("name", name);

        JsoupHtml5TextField subject = new JsoupHtml5TextField(inputElement, formElement);

        assertEquals(name, subject.getName());
    }


    @Test
    public void testGetValue() {

        String value = UUID.randomUUID().toString();
        inputElement.attr("value", value);

        JsoupHtml5TextField subject = new JsoupHtml5TextField(inputElement, formElement);

        assertEquals(value, subject.getValue());
    }

    @Test
    public void testRequired() {

        JsoupHtml5TextField subject = new JsoupHtml5TextField(inputElement, formElement);
        assertFalse(subject.isRequired());

        String value = UUID.randomUUID().toString();
        inputElement.attr("required", value);

        subject = new JsoupHtml5TextField(inputElement, formElement);

        assertTrue(subject.isRequired());
    }

    Random r = new Random();
    @Test
    public void testMaxLength() {

        JsoupHtml5TextField subject = new JsoupHtml5TextField(inputElement, formElement);
        assertNull(subject.getMaxLength());

        inputElement.attr("maxlength", "");

        subject = new JsoupHtml5TextField(inputElement, formElement);
        assertNull(subject.getMaxLength());

        Long maxlength = r.nextLong();
        inputElement.attr("maxlength", maxlength.toString());


        subject = new JsoupHtml5TextField(inputElement, formElement);
        assertEquals(maxlength, subject.getMaxLength());

    }

    @Test
    public void testGetLabel() {

        JsoupHtml5TextField subject = new JsoupHtml5TextField(inputElement, formElement);
        assertNull(subject.getLabel());

        String name = UUID.randomUUID().toString();
        inputElement.attr("name", name);
        Element labelElement = formElement.appendElement("label");

        subject = new JsoupHtml5TextField(inputElement, formElement);
        assertNull(subject.getLabel());

        labelElement.attr("for", name);
        subject = new JsoupHtml5TextField(inputElement, formElement);

        assertEquals("", subject.getLabel());

        String labelText = UUID.randomUUID().toString();
        labelElement.text(labelText);
        subject = new JsoupHtml5TextField(inputElement, formElement);

        assertEquals(labelText, subject.getLabel());

        //add a non-error class
        labelElement.addClass("x");
        subject = new JsoupHtml5TextField(inputElement, formElement);

        assertEquals(labelText, subject.getLabel());

        //add a error class
        labelElement.addClass("error");
        subject = new JsoupHtml5TextField(inputElement, formElement);

        assertNull(subject.getLabel());

    }


    @Test
    public void testHasError() {

        JsoupHtml5TextField subject = new JsoupHtml5TextField(inputElement, formElement);
        assertFalse(subject.hasError());
        assertNull(subject.getErrorMessage());

        String name = UUID.randomUUID().toString();
        inputElement.attr("name", name);
        Element labelElement = formElement.appendElement("label");

        labelElement.attr("for", name);
        String labelText = UUID.randomUUID().toString();
        labelElement.text(labelText);

        subject = new JsoupHtml5TextField(inputElement, formElement);
        assertFalse(subject.hasError());
        assertNull(subject.getErrorMessage());

        Element errorLabelElement = formElement.appendElement("label");

        //before there's an error
        errorLabelElement.attr("for", name);
        errorLabelElement.addClass("x");
        String errorText = UUID.randomUUID().toString();
        errorLabelElement.text(errorText);

        subject = new JsoupHtml5TextField(inputElement, formElement);
        assertFalse(subject.hasError());
        assertNull(subject.getErrorMessage());


        errorLabelElement.addClass("error");
        subject = new JsoupHtml5TextField(inputElement, formElement);
        assertTrue(subject.hasError());
        assertEquals(errorText, subject.getErrorMessage());
    }





}
