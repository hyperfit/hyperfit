package org.hyperfit.resource.html5.controls.form;


import org.hyperfit.Helpers;
import org.hyperfit.exception.HyperfitException;
import org.hyperfit.net.Method;
import org.hyperfit.net.RequestBuilder;
import org.hyperfit.resource.controls.form.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


public class JsoupHtml5FormTest {


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

        JsoupHtml5Form subject = new JsoupHtml5Form(formElement);

        assertEquals(name, subject.getName());
    }


    @Test
    public void testGetHref() {

        String action = UUID.randomUUID().toString();
        formElement.attr("action", action);

        JsoupHtml5Form subject = new JsoupHtml5Form(formElement);

        assertEquals(action, subject.getHref());
    }

    @Test
    public void testGetMethod() {

        String method = Helpers.random("get", "GET", "POST", "post");
        formElement.attr("method", method);

        JsoupHtml5Form subject = new JsoupHtml5Form(formElement);

        assertEquals(Method.valueOf(method.toUpperCase()), subject.getMethod());
    }

    @Test
    public void testGetMethodDefaultsToGET() {
        JsoupHtml5Form subject = new JsoupHtml5Form(formElement);

        assertEquals(Method.GET, subject.getMethod());
    }

    @Test(expected = HyperfitException.class)
    public void testVerifyGetFormNoMatchesThrows(){

        JsoupHtml5Form subject = new JsoupHtml5Form(formElement);
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


        JsoupHtml5Form subject = new JsoupHtml5Form(formElement);

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
        JsoupHtml5Form subject = new JsoupHtml5Form(formElement);

        Field actual = subject.getField(inputName);
        assertTrue(actual instanceof TextField);

        JsoupHtml5TextField expected = new JsoupHtml5TextField(inputElement, formElement);
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
        JsoupHtml5Form subject = new JsoupHtml5Form(formElement);

        Field actual = subject.getField(inputName);
        assertTrue(actual instanceof HiddenField);

        JsoupHtml5HiddenField expected = new JsoupHtml5HiddenField(inputElement, formElement);
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
        JsoupHtml5Form subject = new JsoupHtml5Form(formElement);

        Field actual = subject.getField(inputName);
        assertTrue(actual instanceof EmailField);

        JsoupHtml5EmailField expected = new JsoupHtml5EmailField(inputElement, formElement);
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
        JsoupHtml5Form subject = new JsoupHtml5Form(formElement);

        Field actual = subject.getField(inputName);
        assertTrue(actual instanceof TelephoneNumberField);

        JsoupHtml5TelephoneNumberField expected = new JsoupHtml5TelephoneNumberField(inputElement, formElement);
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
        JsoupHtml5Form subject = new JsoupHtml5Form(formElement);

        Field actual = subject.getField(inputName);
        assertTrue(actual instanceof CheckboxField);

        JsoupHtml5CheckboxField expected = new JsoupHtml5CheckboxField(inputElement, formElement);
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
        JsoupHtml5Form subject = new JsoupHtml5Form(formElement);

        Field actual = subject.getField(inputName);
        assertTrue(actual instanceof SubmitField);

        JsoupHtml5SubmitField expected = new JsoupHtml5SubmitField(inputElement, formElement);
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
        JsoupHtml5Form subject = new JsoupHtml5Form(formElement);

        Field actual = subject.getField(inputName);
        assertTrue(actual instanceof ChoiceField);

        JsoupHtml5ChoiceField expected = new JsoupHtml5ChoiceField(inputElement, formElement);
        assertEquals(expected, actual);

    }


    @Test
    public void testGetFields(){
        Element formElement = Jsoup.parse(
            "<form>" +
            "</form>"
        ).select("form").get(0);


        Element combo1 = formElement.appendElement("select")
            .attr("name", UUID.randomUUID().toString());

        Element combo2 = formElement.appendElement("select")
            .attr("name", UUID.randomUUID().toString());


        Element hidden1 = formElement.appendElement("input")
            .attr("name", UUID.randomUUID().toString())
            .attr("type", "hidden")
        ;

        Element namedSubmit = formElement.appendElement("input")
            .attr("name", UUID.randomUUID().toString())
            .attr("type", "submit")
        ;

        Element unamedSubmit = formElement.appendElement("input")
            .attr("type", "submit")
        ;

        JsoupHtml5Form subject = new JsoupHtml5Form(formElement);



        Field[] actual = subject.getFields();
        Field[] expected = new Field[]{
            JsoupHtml5Field.fieldFactory(unamedSubmit, formElement),
            JsoupHtml5Field.fieldFactory(namedSubmit, formElement),
            JsoupHtml5Field.fieldFactory(hidden1, formElement),
            JsoupHtml5Field.fieldFactory(combo1, formElement),
            JsoupHtml5Field.fieldFactory(combo2, formElement)
        };



        assertThat(actual, arrayContainingInAnyOrder(expected));


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


        JsoupHtml5Form subject = new JsoupHtml5Form(formElement);

        RequestBuilder actual = subject.toRequestBuilder();

        assertEquals(Method.POST, actual.getMethod());
        assertEquals("application/x-www-form-urlencoded", actual.getContentType());
    }
}
