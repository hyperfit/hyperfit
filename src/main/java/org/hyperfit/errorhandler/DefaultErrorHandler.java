package org.hyperfit.errorhandler;


import org.hyperfit.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hyperfit.exception.ServiceException;

/**
 * <p>Default implementation of HyperErrorHandler</p>
 * <p>Throws custom exceptions for error codes that are handled
 * and generic exception for any other one.</p>
 * @see ErrorHandler
 */
public class DefaultErrorHandler implements ErrorHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultErrorHandler.class);
    
    /**
     * {@inheritDoc}
     */
    public RuntimeException handleError(ResponseError error) {
        if(error.getStatusCode() == 401) {
            logErrorHandling(error.getStatusCode(), UnauthorizedException.class, error.getErrorMessage());
            return new UnauthorizedException(error.getErrorMessage());
        }
        if(error.getStatusCode() == 403) {
            logErrorHandling(error.getStatusCode(), ResourceUnavailableException.class, error.getErrorMessage());
            return new ResourceUnavailableException(error.getErrorMessage());
        }
        if(error.getStatusCode() == 404) {
            logErrorHandling(error.getStatusCode(), ResourceNotFoundException.class, error.getErrorMessage());
            return new ResourceNotFoundException(error.getErrorMessage());
        }
        if(error.getStatusCode() == 501) {
            logErrorHandling(error.getStatusCode(), ServiceUnavailableException.class, error.getErrorMessage());
            return new ServiceUnavailableException(error.getErrorMessage());
        }
        
        logErrorHandling(error.getStatusCode(), ServiceException.class, error.getErrorMessage());
        return new ServiceException(error.getErrorMessage());
    }
    
    protected <T> void logErrorHandling(int errorCode, Class<T> exceptionClass, String message) {        
        LOG.debug("Handling error code [{}] with exception class [{}], message: [{}]", errorCode, exceptionClass.getName(), message);        
    }
}
