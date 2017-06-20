package org.hyperfit.resource;


import org.hyperfit.exception.HyperfitException;
import org.slf4j.helpers.MessageFormatter;

/**
 *Exception will be thrown if the expected resource is missing
 *
 */
public class HyperResourceException extends HyperfitException {

    public HyperResourceException(String message) {
        super(message);
    }

    public HyperResourceException(String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage());
    }


    public HyperResourceException(Exception cause, String message) {
        super(message, cause);
    }


}
