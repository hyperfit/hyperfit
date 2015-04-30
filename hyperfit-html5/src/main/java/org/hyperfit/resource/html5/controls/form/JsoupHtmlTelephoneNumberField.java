package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.resource.controls.form.HiddenField;
import org.hyperfit.resource.controls.form.TelephoneNumberField;
import org.jsoup.nodes.Element;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JsoupHtmlTelephoneNumberField extends JsoupHtmlField implements TelephoneNumberField {

    public JsoupHtmlTelephoneNumberField(Element inputElement, Element formElement){
        super(inputElement, formElement);

    }

    @Override
    public String getValue() {
        return null;
    }
}
