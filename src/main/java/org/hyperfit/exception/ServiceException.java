package org.hyperfit.exception;

/**
 * <p>Exception class for all exceptions related with errors received from calling hypermedia 
 * REST endpoints.</p>
 * 
 * <p>If an error code is returned in a response (i.e. 501) an exception of this type, or derived, is
 * thrown to alert the user of the client</p>
 *
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 4040328974398364920L;

    public ServiceException(String message) {
        super(message);
    }
}
