package org.hyperfit.exception;

/**
 * <p>Exception class to handle resource not available errors (403)</p>
 *
 */
public class ResourceUnavailableException extends ServiceException {

    private static final long serialVersionUID = -646428464906657500L;
    
    public ResourceUnavailableException(String message) {
        super(message);
    }
}
