package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.resource.controls.form.SubmitField;
import org.jsoup.nodes.Element;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JsoupHtml5SubmitField extends JsoupHtml5Field implements SubmitField {

    private final String value;
    private final boolean includeOnSubmit;

    public JsoupHtml5SubmitField(Element inputElement, Element formElement){
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


    @Override
    public Long getMaxLength() {
        return null;
    }
}
