package org.hyperfit.resource.html5.controls.form;


import org.hyperfit.Helpers;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.net.BoringRequestBuilder;
import org.hyperfit.net.Method;
import org.hyperfit.net.RequestBuilder;
import org.hyperfit.resource.controls.form.*;
import org.hyperfit.resource.html5.Html5Resource;
import org.hyperfit.resource.html5.controls.link.Html5HyperLink;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class JsoupHtmlFormTest {


    Element formElement;

    @Before
    public void setUp(){
        Document doc = Jsoup.parse("<html>" +
        "<head>" +
        "</head>" +
        "" +
        "<body>" +
        "<form/>" +
        "</body>" +
        "" +
        "" +
        "</html>");


        formElement = doc.select("form").get(0);
    }


    @Test
    public void testGetName() {

        String name = UUID.randomUUID().toString();
        formElement.attr("name", name);

        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        assertEquals(name, subject.getName());
    }


    @Test
    public void testGetHref() {

        String action = UUID.randomUUID().toString();
        formElement.attr("action", action);

        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        assertEquals(action, subject.getHref());
    }

    @Test
    public void testGetMethod() {

        String method = Helpers.random("get", "GET", "POST", "post");
        formElement.attr("method", method);

        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        assertEquals(Method.valueOf(method.toUpperCase()), subject.getMethod());
    }

    @Test
    public void testGetMethodDefaultsToGET() {
        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        assertEquals(Method.GET, subject.getMethod());
    }

    @Test(expected = HyperfitException.class)
    public void testVerifyGetFormNoMatchesThrows(){

        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);
        subject.getField(UUID.randomUUID().toString());

    }


    @Test(expected = HyperfitException.class)
    public void testVerifyGetFormMultipleMatchesThrows(){
        Element formElement = Jsoup.parse(
            "<form>" +
            "" +
            "<input name=\"name\"/>" +
            "<input name=\"name\"/>" +
            "</form>"
        ).select("form").get(0);


        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        subject.getField("name");
    }

    @Test
    public void testGetTextField(){
        Element formElement = Jsoup.parse(
            "<form>" +
            "" +
            "<input/>" +
            "</form>"
        ).select("form").get(0);

        Element inputElement = formElement.select("input").get(0);
        inputElement.attr("type", "text");
        String inputName = UUID.randomUUID().toString();

        inputElement.attr("name", inputName);
        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        Field actual = subject.getField(inputName);
        assertTrue(actual instanceof TextField);

        JsoupHtmlTextField expected = new JsoupHtmlTextField(inputElement, formElement);
        assertEquals(expected, actual);

    }

    @Test
    public void testGetHiddenField(){
        Element formElement = Jsoup.parse(
        "<form>" +
        "" +
        "<input/>" +
        "</form>"
        ).select("form").get(0);

        Element inputElement = formElement.select("input").get(0);
        inputElement.attr("type", "hidden");
        String inputName = UUID.randomUUID().toString();

        inputElement.attr("name", inputName);
        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        Field actual = subject.getField(inputName);
        assertTrue(actual instanceof HiddenField);

        JsoupHtmlHiddenField expected = new JsoupHtmlHiddenField(inputElement, formElement);
        assertEquals(expected, actual);

    }

    @Test
    public void testGetEmailField(){
        Element formElement = Jsoup.parse(
        "<form>" +
        "" +
        "<input/>" +
        "</form>"
        ).select("form").get(0);

        Element inputElement = formElement.select("input").get(0);
        inputElement.attr("type", "email");
        String inputName = UUID.randomUUID().toString();

        inputElement.attr("name", inputName);
        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        Field actual = subject.getField(inputName);
        assertTrue(actual instanceof EmailField);

        JsoupHtmlEmailField expected = new JsoupHtmlEmailField(inputElement, formElement);
        assertEquals(expected, actual);

    }


    @Test
    public void testGetTelephoneNumberField(){
        Element formElement = Jsoup.parse(
        "<form>" +
        "" +
        "<input/>" +
        "</form>"
        ).select("form").get(0);

        Element inputElement = formElement.select("input").get(0);
        inputElement.attr("type", "tel");
        String inputName = UUID.randomUUID().toString();

        inputElement.attr("name", inputName);
        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        Field actual = subject.getField(inputName);
        assertTrue(actual instanceof TelephoneNumberField);

        JsoupHtmlTelephoneNumberField expected = new JsoupHtmlTelephoneNumberField(inputElement, formElement);
        assertEquals(expected, actual);

    }


    @Test
    public void testGetCheckboxField(){
        Element formElement = Jsoup.parse(
        "<form>" +
        "" +
        "<input/>" +
        "</form>"
        ).select("form").get(0);

        Element inputElement = formElement.select("input").get(0);
        inputElement.attr("type", "checkbox");
        String inputName = UUID.randomUUID().toString();

        inputElement.attr("name", inputName);
        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        Field actual = subject.getField(inputName);
        assertTrue(actual instanceof CheckboxField);

        JsoupHtmlCheckboxField expected = new JsoupHtmlCheckboxField(inputElement, formElement);
        assertEquals(expected, actual);

    }


    @Test
    public void testGetSubmitField(){
        Element formElement = Jsoup.parse(
        "<form>" +
        "" +
        "<input/>" +
        "</form>"
        ).select("form").get(0);

        Element inputElement = formElement.select("input").get(0);
        inputElement.attr("type", "submit");
        String inputName = UUID.randomUUID().toString();

        inputElement.attr("name", inputName);
        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        Field actual = subject.getField(inputName);
        assertTrue(actual instanceof SubmitField);

        JsoupHtmlSubmitField expected = new JsoupHtmlSubmitField(inputElement, formElement);
        assertEquals(expected, actual);

    }


    @Test
    public void testGetChoiceField(){
        Element formElement = Jsoup.parse(
        "<form>" +
        "" +
        "<select/>" +
        "</form>"
        ).select("form").get(0);

        Element inputElement = formElement.select("select").get(0);
        String inputName = UUID.randomUUID().toString();

        inputElement.attr("name", inputName);
        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        Field actual = subject.getField(inputName);
        assertTrue(actual instanceof ChoiceField);

        JsoupHtmlChoiceField expected = new JsoupHtmlChoiceField(inputElement, formElement);
        assertEquals(expected, actual);

    }


    @Test
    public void testToRequestBuilder(){
        Element formElement = Jsoup.parse(
            "<form>" +
            "" +
            "</form>"
        ).select("form").get(0);

        String href = UUID.randomUUID().toString();
        formElement.attr("action", href);

        formElement.attr("method", "post");


        JsoupHtmlForm subject = new JsoupHtmlForm(formElement);

        RequestBuilder actual = subject.toRequestBuilder();

        assertEquals(Method.POST, actual.getMethod());
        assertEquals("application/x-www-form-urlencoded", actual.getContentType());
    }
}
