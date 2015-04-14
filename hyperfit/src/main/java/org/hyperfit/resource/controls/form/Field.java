package org.hyperfit.resource.controls.form;

public interface Field {

    String getName();
    String getLabel();

    boolean hasError();

    String getErrorMessage();

    Object getValue();

    boolean isRequired();


}
