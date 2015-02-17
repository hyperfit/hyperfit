package org.hyperfit.exception;

import org.hyperfit.net.Request;
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

    private final Request request;
    private final Response response;

    public ResponseException(String message, Request request, Response response) {
        super(message);
        this.request = request;
        this.response = response;
    }

    public ResponseException(Exception e, String message, Request request, Response response) {
        super(e, message);
        this.request = request;
        this.response = response;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }
}
