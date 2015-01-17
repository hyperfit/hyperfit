package org.hyperfit.exception;

/**
 * <p>Exception class to handle resource not found errors (404)</p>
 *
 */
public class ResourceNotFoundException extends ServiceException {

    private static final long serialVersionUID = 1L;
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
