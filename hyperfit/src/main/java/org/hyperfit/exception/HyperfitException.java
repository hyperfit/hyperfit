package org.hyperfit.exception;

/**
 * Custom exception class for exceptions happening in the HyperClient
 *
 */
public class HyperfitException extends RuntimeException {

    public HyperfitException(String message) {
        super(message);
    }


    public HyperfitException(String message, Exception cause) {
        super(message, cause);
    }


}
