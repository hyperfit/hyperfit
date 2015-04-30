package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.message.Messages;
import org.hyperfit.resource.HyperResourceException;
import org.hyperfit.resource.controls.form.Field;
import org.hyperfit.resource.controls.form.TextField;
import org.jsoup.nodes.Element;

@EqualsAndHashCode
@ToString
public abstract class JsoupHtmlField implements Field {

    protected final String name;

    public JsoupHtmlField(Element inputElement, Element formElement){

        name = inputElement.attr("name");


    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }


    @Override
    public boolean isRequired() {
        return false;
    }

    protected static JsoupHtmlField fieldFactory(Element fieldElement, Element formElement){
        String tagName = fieldElement.tagName().toLowerCase();

        if(tagName.equals("input")){
            String type = fieldElement.attr("type").toLowerCase();

            if(type.equals("text")) {
                return new JsoupHtmlTextField(fieldElement, formElement);
            }

            if(type.equals("hidden")){
                return new JsoupHtmlHiddenField(fieldElement, formElement);
            }

            if(type.equals("email")){
                return new JsoupHtmlEmailField(fieldElement, formElement);
            }

            if(type.equals("tel")){
                return new JsoupHtmlTelephoneNumberField(fieldElement, formElement);
            }

            if(type.equals("checkbox")){
                return new JsoupHtmlCheckboxField(fieldElement, formElement);
            }

            if(type.equals("submit")){
                return new JsoupHtmlSubmitField(fieldElement, formElement);
            }
        }

        if(tagName.equals("select")){
            return new JsoupHtmlChoiceField(fieldElement, formElement);
        }


        throw new HyperResourceException(Messages.MSG_ERROR_FIELD_TYPE_NOT_KNOWN, tagName);
    }
}
