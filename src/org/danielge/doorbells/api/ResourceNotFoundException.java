package org.danielge.doorbells.api;

public class ResourceNotFoundException extends DoorbellsApiException {
    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(Throwable tr) {
        super(tr);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable tr) {
        super(message, tr);
    }
}
