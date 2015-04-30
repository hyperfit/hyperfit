package org.hyperfit.resource.controls.form;

import org.hyperfit.net.Method;
import org.hyperfit.net.RequestBuilder;

public interface Form {

    String getName();

    String getHref();

    Method getMethod();

    //TODO: Field[] getFields();

    Field getField(String fieldName);

    public RequestBuilder toRequestBuilder();

}
