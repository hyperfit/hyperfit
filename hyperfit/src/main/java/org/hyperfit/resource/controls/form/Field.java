package org.hyperfit.resource.controls.form;

public interface Field {

    String getName();

    /**
     *
     * @return text of a label for the field or null if the field does not have a label
     */
    String getLabel();

    boolean hasError();

    String getErrorMessage();

    Object getValue();

    boolean isRequired();


}
