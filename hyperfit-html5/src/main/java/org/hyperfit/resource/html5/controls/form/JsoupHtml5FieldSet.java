package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.resource.controls.form.FieldSet;
import org.hyperfit.utils.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@EqualsAndHashCode
@ToString
public class JsoupHtml5FieldSet implements FieldSet {

    private final String name;
    private final String label;


    public JsoupHtml5FieldSet(Element fieldSetElement, Element formElement) {

        name = fieldSetElement.attr("name");

        String finalLabel = null;
        Elements legendMatches = fieldSetElement.select("legend");
        if (!legendMatches.isEmpty()) {
            finalLabel = legendMatches.get(0).text();
        }


        label = finalLabel;

    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public String getLabel() {
        return label;
    }

}
