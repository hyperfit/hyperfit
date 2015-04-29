package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.resource.controls.form.Field;
import org.hyperfit.resource.controls.form.Form;
import org.jsoup.nodes.Element;

@EqualsAndHashCode
@ToString
public class JsoupHtmlForm implements Form {

    private final String name;

    public JsoupHtmlForm(Element formElement){
        name = formElement.attr("name");

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Field[] getFields() {
        return new Field[0];
    }

    @Override
    public Field getField(String fieldName) {
        return null;
    }
}
