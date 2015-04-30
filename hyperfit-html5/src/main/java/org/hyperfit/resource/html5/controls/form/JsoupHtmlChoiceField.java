package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.resource.controls.form.ChoiceField;
import org.hyperfit.resource.controls.form.EmailField;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JsoupHtmlChoiceField extends JsoupHtmlField implements ChoiceField {

    private final Option[] options;
    private final Option selectedOption;

    public JsoupHtmlChoiceField(Element inputElement, Element formElement){
        super(inputElement, formElement);

        Elements optionElements = inputElement.select("option");

        this.options = new Option[optionElements.size()];

        Option selected = null;
        for(int i = 0; i < optionElements.size(); i++){
            Element option = optionElements.get(i);
            Option o = new Option(option.text(), option.hasAttr("value") ? option.attr("value") : option.text());

            this.options[i] = o;

            if(option.hasAttr("selected")){
                selected = o;
            }

        }

        selectedOption = selected;

    }

    @Override
    public String getValue() {
        return selectedOption == null ? null : selectedOption.getValue();
    }

    @Override
    public Option getSelectedOption() {
        return selectedOption;
    }

    @Override
    public Option[] getOptions() {
        return options.clone();
    }

    @Override
    public Long getMaxLength() {
        return null;
    }
}
