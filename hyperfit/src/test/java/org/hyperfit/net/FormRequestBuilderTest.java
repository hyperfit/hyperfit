package org.hyperfit.net;


import org.hyperfit.resource.controls.form.Form;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class FormRequestBuilderTest {

    @Mock
    Form mockForm;

    RequestBuilder subject;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        this.subject = new FormRequestBuilder(mockForm);
    }


    @Test
    public void testParamSetting(){

    }
}
