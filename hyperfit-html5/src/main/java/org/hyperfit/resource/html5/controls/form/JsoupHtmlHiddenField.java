package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.resource.controls.form.HiddenField;
import org.hyperfit.resource.controls.form.TextField;
import org.jsoup.nodes.Element;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JsoupHtmlHiddenField extends JsoupHtmlField implements HiddenField {

    public JsoupHtmlHiddenField(Element inputElement, Element formElement){
        super(inputElement, formElement);

    }

    @Override
    public String getValue() {
        return null;
    }
}
