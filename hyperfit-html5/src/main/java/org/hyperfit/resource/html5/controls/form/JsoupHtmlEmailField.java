package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.resource.controls.form.EmailField;
import org.hyperfit.resource.controls.form.HiddenField;
import org.jsoup.nodes.Element;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JsoupHtmlEmailField extends JsoupHtmlField implements EmailField {

    public JsoupHtmlEmailField(Element inputElement, Element formElement){
        super(inputElement, formElement);
    }

    @Override
    public String getValue() {
        return null;
    }
}
