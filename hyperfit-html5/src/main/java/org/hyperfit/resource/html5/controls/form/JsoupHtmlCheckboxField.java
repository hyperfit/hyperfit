package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.resource.controls.form.CheckboxField;
import org.hyperfit.resource.controls.form.HiddenField;
import org.jsoup.nodes.Element;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JsoupHtmlCheckboxField extends JsoupHtmlField implements CheckboxField {

    private final String value;
    private final CheckState checkState;

    public JsoupHtmlCheckboxField(Element inputElement, Element formElement){
        super(inputElement, formElement);

        value = inputElement.attr("value");

        checkState = inputElement.hasAttr("checked") ? CheckState.CHECKED : CheckState.UNCHECKED;
    }


    @Override
    public String getValue() {
        return value;
    }

    public CheckboxField.CheckState getCheckState() {
        return checkState;
    }
}
