package org.hyperfit.resource.controls.form;

import org.hyperfit.net.Method;
import org.hyperfit.net.RequestBuilder;

public interface Form {

    String getName();

    String getHref();

    Method getMethod();

    Field[] getFields();

    Field getField(String fieldName);

    FieldSet getFieldSet(String fieldSetName);

    public RequestBuilder toRequestBuilder();

}
