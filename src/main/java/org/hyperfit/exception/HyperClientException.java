package org.hyperfit.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * <p>Custom exception class for exceptions happening in the HyperClient</p>
 *
 */
public class HyperClientException extends RuntimeException {

    public HyperClientException(String message) {
        super(message);
    }

    public HyperClientException(String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage());
    }

    public HyperClientException(Exception cause, String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage(), cause);
    }

    public HyperClientException(Exception cause, String message) {
        super(message, cause);
    }

    public HyperClientException(Exception cause) {
        super(cause);
    }

}
