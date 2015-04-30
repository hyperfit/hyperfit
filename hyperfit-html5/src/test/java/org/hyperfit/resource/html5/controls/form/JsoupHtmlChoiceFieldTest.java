package org.hyperfit.resource.html5.controls.form;


import org.hyperfit.resource.controls.form.ChoiceField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


public class JsoupHtmlChoiceFieldTest {


    Element formElement;
    Element selectElement;
    @Before
    public void setUp(){
        Document doc = Jsoup.parse("<html>" +
        "<head>" +
        "</head>" +
        "" +
        "<body>" +
        "<form>" +
        "<select/>" +
        "</form>" +
        "</body>" +
        "" +
        "" +
        "</html>");


        formElement = doc.select("form").get(0);
        selectElement = doc.select("select").get(0);
    }


    @Test
    public void testGetName() {

        String name = UUID.randomUUID().toString();
        selectElement.attr("name", name);

        JsoupHtml5ChoiceField subject = new JsoupHtml5ChoiceField(selectElement, formElement);

        assertEquals(name, subject.getName());
    }


    @Test
    public void testGetValue() {

        JsoupHtml5ChoiceField subject = new JsoupHtml5ChoiceField(selectElement, formElement);
        assertNull(subject.getValue());
        assertNull(subject.getSelectedOption());


        Element option1 = selectElement.appendElement("option");

        subject = new JsoupHtml5ChoiceField(selectElement, formElement);

        assertNull(subject.getValue());
        assertNull(subject.getSelectedOption());

        String value = UUID.randomUUID().toString();
        option1.attr("value", value);
        String optionLabel = UUID.randomUUID().toString();
        option1.text(optionLabel);

        subject = new JsoupHtml5ChoiceField(selectElement, formElement);

        assertNull(subject.getValue());
        assertNull(subject.getSelectedOption());


        option1.attr("selected", UUID.randomUUID().toString());
        subject = new JsoupHtml5ChoiceField(selectElement, formElement);

        assertEquals(value, subject.getValue());
        assertEquals(new ChoiceField.Option(optionLabel, value), subject.getSelectedOption());


    }

    @Test
    public void testRequired() {

        JsoupHtml5ChoiceField subject = new JsoupHtml5ChoiceField(selectElement, formElement);
        assertFalse(subject.isRequired());

        String value = UUID.randomUUID().toString();
        selectElement.attr("required", value);

        subject = new JsoupHtml5ChoiceField(selectElement, formElement);

        assertTrue(subject.isRequired());
    }

    Random r = new Random();
    @Test
    public void testMaxLength() {

        JsoupHtml5ChoiceField subject = new JsoupHtml5ChoiceField(selectElement, formElement);
        assertNull(subject.getMaxLength());

        selectElement.attr("maxlength", "");

        subject = new JsoupHtml5ChoiceField(selectElement, formElement);
        assertNull(subject.getMaxLength());

        Long maxlength = r.nextLong();
        selectElement.attr("maxlength", maxlength.toString());


        subject = new JsoupHtml5ChoiceField(selectElement, formElement);
        assertNull(subject.getMaxLength());

    }

    @Test
    public void testGetLabel() {

        JsoupHtml5ChoiceField subject = new JsoupHtml5ChoiceField(selectElement, formElement);
        assertNull(subject.getLabel());

        String name = UUID.randomUUID().toString();
        selectElement.attr("name", name);
        Element labelElement = formElement.appendElement("label");

        subject = new JsoupHtml5ChoiceField(selectElement, formElement);
        assertNull(subject.getLabel());

        labelElement.attr("for", name);
        subject = new JsoupHtml5ChoiceField(selectElement, formElement);

        assertEquals("", subject.getLabel());

        String labelText = UUID.randomUUID().toString();
        labelElement.text(labelText);
        subject = new JsoupHtml5ChoiceField(selectElement, formElement);

        assertEquals(labelText, subject.getLabel());

        //add a non-error class
        labelElement.addClass("x");
        subject = new JsoupHtml5ChoiceField(selectElement, formElement);

        assertEquals(labelText, subject.getLabel());

        //add a error class
        labelElement.addClass("error");
        subject = new JsoupHtml5ChoiceField(selectElement, formElement);

        assertNull(subject.getLabel());

    }


    @Test
    public void testHasError() {

        JsoupHtml5ChoiceField subject = new JsoupHtml5ChoiceField(selectElement, formElement);
        assertFalse(subject.hasError());
        assertNull(subject.getErrorMessage());

        String name = UUID.randomUUID().toString();
        selectElement.attr("name", name);
        Element labelElement = formElement.appendElement("label");

        labelElement.attr("for", name);
        String labelText = UUID.randomUUID().toString();
        labelElement.text(labelText);

        subject = new JsoupHtml5ChoiceField(selectElement, formElement);
        assertFalse(subject.hasError());
        assertNull(subject.getErrorMessage());

        Element errorLabelElement = formElement.appendElement("label");

        //before there's an error
        errorLabelElement.attr("for", name);
        errorLabelElement.addClass("x");
        String errorText = UUID.randomUUID().toString();
        errorLabelElement.text(errorText);

        subject = new JsoupHtml5ChoiceField(selectElement, formElement);
        assertFalse(subject.hasError());
        assertNull(subject.getErrorMessage());


        errorLabelElement.addClass("error");
        subject = new JsoupHtml5ChoiceField(selectElement, formElement);
        assertTrue(subject.hasError());
        assertEquals(errorText, subject.getErrorMessage());
    }



    @Test
    public void testGetOptions() {

        Element option1 = selectElement.appendElement("option");
        String value1 = UUID.randomUUID().toString();
        option1.attr("value", value1);
        String label1 = UUID.randomUUID().toString();
        option1.text(label1);


        Element option2 = selectElement.appendElement("option");
        String label2 = UUID.randomUUID().toString();
        option2.text(label2);


        JsoupHtml5ChoiceField subject = new JsoupHtml5ChoiceField(selectElement, formElement);

        ChoiceField.Option[] expected = new ChoiceField.Option[]{
            new ChoiceField.Option(label1, value1),
            new ChoiceField.Option(label2, label2),
        };

        assertThat(subject.getOptions(), arrayContainingInAnyOrder(expected));


    }

}
