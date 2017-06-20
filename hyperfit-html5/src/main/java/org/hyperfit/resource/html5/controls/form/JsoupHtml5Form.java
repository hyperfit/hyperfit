package org.hyperfit.resource.html5.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hyperfit.net.FormRequestBuilder;
import org.hyperfit.net.Method;
import org.hyperfit.net.RequestBuilder;
import org.hyperfit.resource.HyperResourceException;
import org.hyperfit.resource.controls.form.Field;
import org.hyperfit.resource.controls.form.FieldSet;
import org.hyperfit.resource.controls.form.Form;
import org.hyperfit.utils.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

@EqualsAndHashCode
@ToString
public class JsoupHtml5Form implements Form {

    private final String name;
    private final String href;
    private final Method method;
    private final Element formElement;

    private final HashMap<String, Field> fieldCache = new HashMap<String, Field>();
    private final HashMap<String, FieldSet> fieldSetCache = new HashMap<String, FieldSet>();

    public JsoupHtml5Form(Element formElement){
        if(!formElement.tagName().equalsIgnoreCase("form")){
            throw new HyperResourceException("formElement must be a form tag");
        }

        this.formElement = formElement;

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



    private static final String fieldSelector = "input[name=%s], select[name=%s]";

    @Override
    public Field getField(String fieldName) {
        if (StringUtils.isEmpty(fieldName)) {
            throw new IllegalArgumentException("fieldName is required");
        }

        if(!fieldCache.containsKey(fieldName)){
            String selector = String.format(fieldSelector, fieldName, fieldName);

            Elements matches = formElement.select(selector);

            if(matches.size() == 0){
                throw new HyperResourceException("Could not find a field with name [" + fieldName + "]");
            }

            if (matches.size() > 1) {
                throw new HyperResourceException("Found more than one field with name [" + fieldName + "]");
            }

            fieldCache.put(fieldName, JsoupHtml5Field.fieldFactory(matches.get(0), formElement));
        }

        return fieldCache.get(fieldName);
    }

    @Override
    public boolean hasField(String fieldName) {
        if (StringUtils.isEmpty(fieldName)) {
            throw new IllegalArgumentException("fieldName is required");
        }

        String selector = String.format(fieldSelector, fieldName, fieldName);
        Elements matches = formElement.select(selector);
        return !matches.isEmpty();

    }

    @Override
    public Field[] getFields() {
        Elements fieldElements = formElement.select("input,select");
        ArrayList<Field> fields = new ArrayList<Field>(fieldCache.values());



        for(Element fieldElement : fieldElements) {
            String fieldName = fieldElement.attr("name");
            //this intentionally only stores the first form with a given name
            if(!fieldCache.containsKey(fieldName)){
                JsoupHtml5Field field = JsoupHtml5Field.fieldFactory(fieldElement, formElement);
                fields.add(field);

                if(!StringUtils.isEmpty(fieldName)){
                    //hmmmm TODO: how can we cache unnamed fields?
                    fieldCache.put(fieldName, field);
                }

            }

        }

        return fields.toArray(new Field[fields.size()]);

    }

    private static final String fieldSetSelector = "fieldset[name=%s]";
    @Override
    public FieldSet getFieldSet(String fieldSetName) {
        if (StringUtils.isEmpty(fieldSetName)) {
            throw new IllegalArgumentException("The provided element is not a form");
        }

        if(!fieldSetCache.containsKey(fieldSetName)){
            String selector = String.format(fieldSetSelector, fieldSetName);

            Elements matches = formElement.select(selector);

            if(matches.size() == 0){
                throw new HyperResourceException("Could not find a field set with name [" + fieldSetName + "]");
            }

            if (matches.size() > 1) {
                throw new HyperResourceException("Found more than one field set with name [" + fieldSetName + "]");
            }

            fieldSetCache.put(fieldSetName, new JsoupHtml5FieldSet(matches.get(0), formElement));
        }

        return fieldSetCache.get(fieldSetName);
    }

    @Override
    public RequestBuilder toRequestBuilder() {
        return new FormRequestBuilder(this);
    }
}
