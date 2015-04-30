package org.hyperfit.resource.controls.form;

public interface CheckboxField extends Field {

    public enum CheckState {
        CHECKED,
        UNCHECKED,
        ;
        //TODO: support indeterminate?
    }

    String getValue();

    CheckState getCheckState();

}
