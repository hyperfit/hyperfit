package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.resource.HyperResourceException;
import org.hyperfit.resource.controls.form.Field;
import org.hyperfit.utils.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@EqualsAndHashCode
@ToString
public abstract class JsoupHtml5Field implements Field {

    private final String name;
    private final boolean required;
    private final String label;
    private final String errorMessage;
    private final Long maxLength;

    private final static String labelMatcher = "label[for=%s]";
    public JsoupHtml5Field(Element inputElement, Element formElement){

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

        Long finalMaxLength = null;
        if(inputElement.hasAttr("maxlength")){
            try{
               finalMaxLength = Long.parseLong(inputElement.attr("maxlength"));
            } catch (Exception e){

            }
        }

        maxLength = finalMaxLength;



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

    @Override
    public Long getMaxLength() {
        return maxLength;
    }

    protected static JsoupHtml5Field fieldFactory(Element fieldElement, Element formElement){
        String tagName = fieldElement.tagName().toLowerCase();

        if(tagName.equals("input")){
            String type = fieldElement.attr("type").toLowerCase();

            if(type.equals("text")) {
                return new JsoupHtml5TextField(fieldElement, formElement);
            }

            if(type.equals("hidden")){
                return new JsoupHtml5HiddenField(fieldElement, formElement);
            }

            if(type.equals("email")){
                return new JsoupHtml5EmailField(fieldElement, formElement);
            }

            if(type.equals("tel")){
                return new JsoupHtml5TelephoneNumberField(fieldElement, formElement);
            }

            if(type.equals("checkbox")){
                return new JsoupHtml5CheckboxField(fieldElement, formElement);
            }

            if(type.equals("submit")){
                return new JsoupHtml5SubmitField(fieldElement, formElement);
            }
        }

        if(tagName.equals("select")){
            return new JsoupHtml5ChoiceField(fieldElement, formElement);
        }


        throw new HyperResourceException("Field type for tag [" + tagName + "] is not known", tagName);
    }
}
