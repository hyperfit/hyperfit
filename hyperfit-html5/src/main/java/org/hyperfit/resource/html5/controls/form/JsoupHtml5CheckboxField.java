package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.resource.controls.form.CheckboxField;
import org.jsoup.nodes.Element;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JsoupHtml5CheckboxField extends JsoupHtml5Field implements CheckboxField {

    private final String value;
    private final CheckState checkState;

    public JsoupHtml5CheckboxField(Element inputElement, Element formElement){
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

    @Override
    public Long getMaxLength() {
        return null;
    }
}
