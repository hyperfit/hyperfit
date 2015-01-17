package org.hyperfit.exception;

/**
 * <p>Exception class to handle service not available errors (501)</p>
 *
 */
public class ServiceUnavailableException extends ServiceException {

    private static final long serialVersionUID = -327895396900318319L;

    public ServiceUnavailableException(String message) {
        super(message);
    }
}
