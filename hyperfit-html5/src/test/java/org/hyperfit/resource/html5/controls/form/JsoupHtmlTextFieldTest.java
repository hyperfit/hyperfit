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
import org.mockito.internal.util.collections.Sets;

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

        JsoupHtmlTextField subject = new JsoupHtmlTextField(inputElement, formElement);

        assertEquals(name, subject.getName());
    }


    @Test
    public void testGetValue() {

        String value = UUID.randomUUID().toString();
        inputElement.attr("value", value);

        JsoupHtmlTextField subject = new JsoupHtmlTextField(inputElement, formElement);

        assertEquals(value, subject.getValue());
    }

    @Test
    public void testRequired() {

        JsoupHtmlTextField subject = new JsoupHtmlTextField(inputElement, formElement);
        assertFalse(subject.isRequired());

        String value = UUID.randomUUID().toString();
        inputElement.attr("required", value);

        subject = new JsoupHtmlTextField(inputElement, formElement);

        assertTrue(subject.isRequired());
    }

    @Test
    public void testGetLabel() {

        JsoupHtmlTextField subject = new JsoupHtmlTextField(inputElement, formElement);
        assertNull(subject.getLabel());

        String name = UUID.randomUUID().toString();
        inputElement.attr("name", name);
        Element labelElement = formElement.appendElement("label");

        subject = new JsoupHtmlTextField(inputElement, formElement);
        assertNull(subject.getLabel());

        labelElement.attr("for", name);
        subject = new JsoupHtmlTextField(inputElement, formElement);

        assertEquals("", subject.getLabel());

        String labelText = UUID.randomUUID().toString();
        labelElement.text(labelText);
        subject = new JsoupHtmlTextField(inputElement, formElement);

        assertEquals(labelText, subject.getLabel());

        //add a non-error class
        labelElement.addClass("x");
        subject = new JsoupHtmlTextField(inputElement, formElement);

        assertEquals(labelText, subject.getLabel());

        //add a error class
        labelElement.addClass("error");
        subject = new JsoupHtmlTextField(inputElement, formElement);

        assertNull(subject.getLabel());

    }


    @Test
    public void testHasError() {

        JsoupHtmlTextField subject = new JsoupHtmlTextField(inputElement, formElement);
        assertFalse(subject.hasError());
        assertNull(subject.getErrorMessage());

        String name = UUID.randomUUID().toString();
        inputElement.attr("name", name);
        Element labelElement = formElement.appendElement("label");

        labelElement.attr("for", name);
        String labelText = UUID.randomUUID().toString();
        labelElement.text(labelText);

        subject = new JsoupHtmlTextField(inputElement, formElement);
        assertFalse(subject.hasError());
        assertNull(subject.getErrorMessage());

        Element errorLabelElement = formElement.appendElement("label");

        //before there's an error
        errorLabelElement.attr("for", name);
        errorLabelElement.addClass("x");
        String errorText = UUID.randomUUID().toString();
        errorLabelElement.text(errorText);

        subject = new JsoupHtmlTextField(inputElement, formElement);
        assertFalse(subject.hasError());
        assertNull(subject.getErrorMessage());


        errorLabelElement.addClass("error");
        subject = new JsoupHtmlTextField(inputElement, formElement);
        assertTrue(subject.hasError());
        assertEquals(errorText, subject.getErrorMessage());
    }





}
