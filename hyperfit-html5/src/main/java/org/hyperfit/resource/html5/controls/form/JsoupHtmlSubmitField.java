package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.resource.controls.form.HiddenField;
import org.hyperfit.resource.controls.form.SubmitField;
import org.jsoup.nodes.Element;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JsoupHtmlSubmitField extends JsoupHtmlField implements SubmitField {

    private final String value;
    private final boolean includeOnSubmit;

    public JsoupHtmlSubmitField(Element inputElement, Element formElement){
        super(inputElement, formElement);

        value = inputElement.attr("value");

        //html only includes submit when name is present
        includeOnSubmit = inputElement.hasAttr("name");

    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getLabel() {
        return value;
    }

    @Override
    public boolean includeOnSubmit() {
        return includeOnSubmit;
    }
}
