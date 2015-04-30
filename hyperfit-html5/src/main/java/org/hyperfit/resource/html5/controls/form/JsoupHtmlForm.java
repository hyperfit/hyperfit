package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.net.Method;
import org.hyperfit.resource.controls.form.Field;
import org.hyperfit.resource.controls.form.Form;
import org.hyperfit.utils.StringUtils;
import org.jsoup.nodes.Element;

@EqualsAndHashCode
@ToString
public class JsoupHtmlForm implements Form {

    private final String name;
    private final String href;
    private final Method method;

    public JsoupHtmlForm(Element formElement){
        name = formElement.attr("name");
        href = formElement.attr("action");

        String formMethod = formElement.attr("method");
        method = StringUtils.isEmpty(formMethod) ? Method.GET : Method.valueOf(formMethod.toUpperCase());

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getHref() {
        return href;
    }

    @Override
    public Method getMethod() {
        return method;
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
