package org.hyperfit.errorhandler;

import org.slf4j.helpers.MessageFormatter;

/**
 * <p>Bean class having all the information related to hyper media response errors.</p>
 *
 */
public class ResponseError {

    private int statusCode;
    private String errorTitle;
    private String errorMessage;
    
    public ResponseError(int statusCode, String errorTitle, String errorMessage) {
        this.statusCode = statusCode;
        this.errorTitle = errorTitle;
        this.errorMessage = errorMessage;
    }

    public ResponseError(int statusCode, String errorMessage) {
        this(statusCode, "", errorMessage);
    }

    public ResponseError(int statusCode, String errorTitle, String errorMessage, Object... args) {
        this(statusCode, errorTitle, MessageFormatter.arrayFormat(errorMessage, args).getMessage());
    }

    public ResponseError(int statusCode, String errorMessage, Object... args) {
        this(statusCode, "", errorMessage, args);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public ResponseError setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public String getErrorTitle() {
        return errorTitle;
    }

    public ResponseError setErrorTitle(String errorTitle) {
        this.errorTitle = errorTitle;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ResponseError setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public ResponseError setErrorMessage(String errorMessage, Object... args) {
        return setErrorMessage(MessageFormatter.arrayFormat(errorMessage, args).getMessage());
    }

}
