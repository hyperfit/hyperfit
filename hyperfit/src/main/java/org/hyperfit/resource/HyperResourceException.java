package org.hyperfit.resource;

import org.hyperfit.exception.HyperfitException;

/**
 *Exception will be thrown if the expected resource is missing
 *
 */
public class HyperResourceException extends HyperfitException {

    public HyperResourceException(String message) {
        super(message);
    }



    public HyperResourceException(String message, Exception cause) {
        super(message, cause);
    }


}
