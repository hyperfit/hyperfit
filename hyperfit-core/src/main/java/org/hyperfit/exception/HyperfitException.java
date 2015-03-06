package org.hyperfit.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * Custom exception class for exceptions happening in the HyperClient
 *
 */
public class HyperfitException extends RuntimeException {

    public HyperfitException(String message) {
        super(message);
    }

    public HyperfitException(String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage());
    }

    public HyperfitException(Exception cause, String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage(), cause);
    }

    public HyperfitException(Exception cause, String message) {
        super(message, cause);
    }

    public HyperfitException(Exception cause) {
        super(cause);
    }

}
