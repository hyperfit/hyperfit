package org.hyperfit.resource.controls.form;

import org.hyperfit.net.Method;

public interface Form {

    String getName();

    String getHref();

    Method getMethod();

    Field[] getFields();

    Field getField(String fieldName);

}
