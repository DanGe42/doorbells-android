package org.danielge.doorbells.api;

public class BadRequestException extends DoorbellsApiException {
    public BadRequestException() {
        super();
    }

    public BadRequestException(Throwable tr) {
        super(tr);
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable tr) {
        super(message, tr);
    }
}
