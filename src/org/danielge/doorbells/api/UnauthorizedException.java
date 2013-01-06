package org.danielge.doorbells.api;

public class UnauthorizedException extends DoorbellsApiException {
    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(Throwable tr) {
        super(tr);
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable tr) {
        super(message, tr);
    }
}
