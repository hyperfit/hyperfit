package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.resource.controls.form.ChoiceField;
import org.hyperfit.resource.controls.form.EmailField;
import org.jsoup.nodes.Element;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JsoupHtmlChoiceField extends JsoupHtmlField implements ChoiceField {

    public JsoupHtmlChoiceField(Element inputElement, Element formElement){
        super(inputElement, formElement);
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public Option getSelectedOption() {
        return null;
    }

    @Override
    public Option[] getOptions() {
        return new Option[0];
    }
}
