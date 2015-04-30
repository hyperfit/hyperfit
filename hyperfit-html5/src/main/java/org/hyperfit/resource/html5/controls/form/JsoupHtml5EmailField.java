package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.resource.controls.form.EmailField;
import org.jsoup.nodes.Element;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JsoupHtml5EmailField extends JsoupHtml5Field implements EmailField {

    private final String value;

    public JsoupHtml5EmailField(Element inputElement, Element formElement){
        super(inputElement, formElement);

        value = inputElement.attr("value");
    }

    @Override
    public String getValue() {
        return value;
    }
}
