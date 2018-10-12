package org.hyperfit.exception;


/**
 * this exception will throw when there is no client to handle request's scheme
 */
public class NoClientRegisteredForSchemeException extends HyperfitException {


    public NoClientRegisteredForSchemeException(String unsupportedScheme) {
        super("No HyperClient has been registered for the " + unsupportedScheme +  " scheme");
    }

}
