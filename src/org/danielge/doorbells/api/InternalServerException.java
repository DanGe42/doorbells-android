package org.danielge.doorbells.api;

public class InternalServerException extends DoorbellsApiException {
    public InternalServerException() {
        super();
    }

    public InternalServerException(Throwable tr) {
        super(tr);
    }

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String message, Throwable tr) {
        super(message, tr);
    }
}
