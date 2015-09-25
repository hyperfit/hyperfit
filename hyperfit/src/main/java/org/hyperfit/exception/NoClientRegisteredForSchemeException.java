package org.hyperfit.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * this exception will throw when there is no client to handle request's scheme
 */
public class NoClientRegisteredForSchemeException extends  HyperfitException {
    public NoClientRegisteredForSchemeException(String message) {
        super(message);
    }

    public NoClientRegisteredForSchemeException(String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage());
    }

    public NoClientRegisteredForSchemeException(Exception cause, String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage(), cause);
    }

    public NoClientRegisteredForSchemeException(Exception cause, String message) {
        super(message, cause);
    }

    public NoClientRegisteredForSchemeException(Exception cause) {
        super(cause);
    }
}
