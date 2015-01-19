package org.hyperfit.errorhandler;

/**
 * <p>Interface defining contract for an error handler</p>
 * <p>A response from hypermedia API can return an error code (i.e. 404) that has to be handled properly</p>
 * <p>The error handler is configured in the HyperClient</p>
 *
 */
public interface ErrorHandler {
    
    /**
     * Handler an hypermedia error.
     * @param error Hyper response error
     * @return a runtime exception for the error code.
     */
    RuntimeException handleError(ResponseError error);
    
}
