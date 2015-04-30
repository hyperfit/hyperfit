package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.resource.controls.form.CheckboxField;
import org.hyperfit.resource.controls.form.HiddenField;
import org.jsoup.nodes.Element;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JsoupHtmlCheckboxField extends JsoupHtmlField implements CheckboxField {


    public JsoupHtmlCheckboxField(Element inputElement, Element formElement){
        super(inputElement, formElement);

    }


    @Override
    public CheckState getValue() {
        return null;
    }
}
