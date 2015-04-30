package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.net.Method;
import org.hyperfit.resource.controls.form.Field;
import org.hyperfit.resource.controls.form.Form;
import org.hyperfit.resource.controls.form.TextField;
import org.hyperfit.utils.StringUtils;
import org.jsoup.nodes.Element;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JsoupHtmlTextField extends JsoupHtmlField implements TextField {

    public JsoupHtmlTextField(Element inputElement, Element formElement){
        super(inputElement, formElement);

    }

    @Override
    public String getValue() {
        return null;
    }
}
