package org.hyperfit.exception;

/**
 * <p>Exception class to handle authentication & authorization errors (401)</p>
 *
 */
public class UnauthorizedException extends ServiceException {

    private static final long serialVersionUID = 1901428305951487789L;

    public UnauthorizedException(String message) {
        super(message);
    }
}
