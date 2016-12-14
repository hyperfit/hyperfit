package org.hyperfit.exception;

import org.hyperfit.net.Response;

/**
 * <p>Exception class for all exceptions related with errors received from calling hypermedia 
 * REST endpoints.</p>
 * 
 * <p>If an error code is returned in a response (i.e. 501) an exception of this type, or derived, is
 * thrown to alert the user of the client</p>
 *
 */
public class ResponseException extends HyperfitException {

    private static final long serialVersionUID = 4040328974398364920L;

    private final Response response;

    public ResponseException(String message, Response response) {
        super(message);
        this.response = response;
    }

    public ResponseException(Exception e, String message, Response response) {
        super(e, message);
        this.response = response;
    }


    public Response getResponse() {
        return response;
    }
}
