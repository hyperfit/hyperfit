package org.hyperfit.resource.controls.form;

import lombok.EqualsAndHashCode;
import lombok.ToString;

public interface ChoiceField extends Field {

    @EqualsAndHashCode
    @ToString
    public class Option {
        private final String value;
        private final String label;

        public Option(String label, String value){
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }
    }

    String getValue();

    Option getSelectedOption();

    Option[] getOptions();

}
