package org.hyperfit.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * <p>Custom exception class for exceptions related with unability to resolve
 * parameterized types.</p>
 *
 */
public class ParameterizedTypeException extends HyperfitException {

    public ParameterizedTypeException(String message) {
        super(message);
    }

    public ParameterizedTypeException(String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage());
    }

    public ParameterizedTypeException(Exception cause, String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage(), cause);
    }

    public ParameterizedTypeException(Exception cause, String message) {
        super(message, cause);
    }

    public ParameterizedTypeException(Exception cause) {
        super(cause);
    }

}
