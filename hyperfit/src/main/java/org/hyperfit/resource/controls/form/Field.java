package org.hyperfit.resource.controls.form;

public interface Field {

    String getName();

    /**
     *
     * @return text of a label for the field or null if the field does not have a label
     */
    String getLabel();

    /**
     *
     * @return true if the field has a validation error associated with it. false otherwise.
     */
    boolean hasError();

    String getErrorMessage();

    Object getValue();

    /**
     *
     * @return true if this field is required for the form to be submitted, false otherwise
     */
    boolean isRequired();

    /**
     *
     * @return the maximum length a value may contain.  Null if no max length is available
     */
    Long getMaxLength();


}
