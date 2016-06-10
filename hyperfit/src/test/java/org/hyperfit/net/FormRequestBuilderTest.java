package org.hyperfit.net;


import org.hyperfit.exception.HyperfitException;
import org.hyperfit.resource.controls.form.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static test.TestUtils.*;


public class FormRequestBuilderTest {

    @Mock
    Form mockForm;

    @Mock
    TextField mockTextField;


    @Mock
    ChoiceField mockChoiceField;


    @Mock
    CheckboxField mockCheckboxField;


    RequestBuilder subject;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        when(mockForm.getFields()).thenReturn(new Field[0]);

        this.subject = new FormRequestBuilder(mockForm);


    }


    @Test(expected = HyperfitException.class)
    public void testParamSettingFieldNotPresent(){
        String paramName = uniqueString();
        String value = uniqueString();

        //TODO: maybe we should have a hasField that we could check in builder..i dunno
        when(mockForm.getField(paramName))
            .thenThrow(new HyperfitException("blah"));

        assertNull(subject.getParam(paramName));

        subject.setParam(paramName, value);

        assertNull(subject.getParam(paramName));
    }


    @Test
    public void testParamSettingFromConstructorTextField(){

        Field[] fields = new Field[]{
            mockTextField
        };

        when(mockForm.getFields())
            .thenReturn(fields);

        this.subject = new FormRequestBuilder(mockForm);

        String textFieldValue = uniqueString();
        when(mockTextField.getValue()).thenReturn(textFieldValue);

        assertThat("because field name is null, value should not be set", subject.getParams().keySet(), empty());


        String textFieldName = uniqueString();
        when(mockTextField.getName()).thenReturn(textFieldName);

        this.subject = new FormRequestBuilder(mockForm);

        assertEquals(textFieldValue, subject.getParam(textFieldName));
    }


    @Test
    public void testParamSettingFromConstructorCheckboxField(){

        Field[] fields = new Field[]{
            mockCheckboxField
        };

        when(mockForm.getFields())
        .thenReturn(fields);

        String fieldName = uniqueString();
        String fieldValue = uniqueString();
        when(mockCheckboxField.getName()).thenReturn(fieldName);
        when(mockCheckboxField.getValue()).thenReturn(fieldValue);

        this.subject = new FormRequestBuilder(mockForm);

        assertNull("because checkbox isn't checked, it should be null", subject.getParam(fieldName));

        when(mockCheckboxField.getCheckState()).thenReturn(CheckboxField.CheckState.CHECKED);
        this.subject = new FormRequestBuilder(mockForm);

        assertEquals(fieldValue, subject.getParam(fieldName));

    }


    @Test
    public void testParamSettingTextField(){
        String paramName = uniqueString();
        String value = uniqueString();

        when(mockForm.getField(paramName))
            .thenReturn(mockTextField);

        subject.setParam(paramName, value);

        assertEquals(value, subject.getParam(paramName));
    }

    @Test
    public void testParamSettingCheckboxField(){
        String paramName = uniqueString();
        String value = uniqueString();

        when(mockForm.getField(paramName))
            .thenReturn(mockCheckboxField);

        when(mockCheckboxField.getValue())
            .thenReturn(value);

        subject.setParam(paramName, CheckboxField.CheckState.UNCHECKED);

        String expected = "";
        assertEquals(expected, subject.getContent());

        subject.setParam(paramName, CheckboxField.CheckState.CHECKED);

        expected =  paramName + "=" + value;
        assertEquals(expected, subject.getContent());
    }


    @Test
    public void testGetContentWithManyParamsSet(){
        String paramName1 = uniqueString();
        String value1 = uniqueString();

        String paramName2 = uniqueString();
        String value2 = uniqueString();

        String paramName3 = uniqueString();
        String value3 = uniqueString();

        when(mockForm.getField(anyString()))
            .thenReturn(mockTextField);

        subject.setParam(paramName1, value1);
        subject.setParam(paramName2, value2);
        subject.setParam(paramName3, value3);

        String expected = paramName1 + "=" + value1 + "&" + paramName2 + "=" + value2 + "&" + paramName3 + "=" + value3;
        assertEquals(expected, subject.getContent());

    }
}
