package org.hyperfit.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * this exception will throw when there is no client to handle request's scheme
 */
public class NoClientRegisteredForScheme extends  HyperfitException {
    public NoClientRegisteredForScheme(String message) {
        super(message);
    }

    public NoClientRegisteredForScheme(String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage());
    }

    public NoClientRegisteredForScheme(Exception cause, String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage(), cause);
    }

    public NoClientRegisteredForScheme(Exception cause, String message) {
        super(message, cause);
    }

    public NoClientRegisteredForScheme(Exception cause) {
        super(cause);
    }
}
