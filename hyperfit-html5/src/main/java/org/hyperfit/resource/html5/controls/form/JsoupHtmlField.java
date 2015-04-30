package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.message.Messages;
import org.hyperfit.resource.HyperResourceException;
import org.hyperfit.resource.controls.form.Field;
import org.hyperfit.resource.controls.form.TextField;
import org.hyperfit.utils.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@EqualsAndHashCode
@ToString
public abstract class JsoupHtmlField implements Field {

    private final String name;
    private final boolean required;
    private final String label;
    private final String errorMessage;

    private final static String labelMatcher = "label[for=%s]";
    public JsoupHtmlField(Element inputElement, Element formElement){

        name = inputElement.attr("name");
        required = inputElement.hasAttr("required");


        String finalLabel = null;
        String finalErrorMessage = null;
        if(!StringUtils.isEmpty(name)) {
            Elements labelMatch = formElement.select(String.format(labelMatcher, name));
            if (!labelMatch.isEmpty()) {
                for (Element match : labelMatch) {
                    if (match.classNames().contains("error")) {
                        finalErrorMessage = match.text();
                    } else {
                        finalLabel = match.text();
                    }
                }
            }
        }

        label = finalLabel;
        errorMessage = finalErrorMessage;

    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public boolean hasError() {
        return errorMessage != null;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }


    @Override
    public boolean isRequired() {
        return required;
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
