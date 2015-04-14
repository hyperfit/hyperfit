package org.hyperfit.resource.controls.form;

public interface Form {

    String getName();

    Field[] getFields();

    Field getField(String fieldName);

}
