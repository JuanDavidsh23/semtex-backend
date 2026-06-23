package com.semtex.shared.web.exception;

/** La operación está prohibida para el actor actual. HTTP 403. */
public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
