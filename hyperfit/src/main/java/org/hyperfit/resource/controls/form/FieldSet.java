package org.hyperfit.resource.controls.form;

public interface FieldSet {

    String getName();

    /**
     *
     * @return text of a label for the field or null if the field does not have a label
     */
    String getLabel();

}
