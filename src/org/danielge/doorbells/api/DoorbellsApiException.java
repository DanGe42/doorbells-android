package org.danielge.doorbells.api;

public class DoorbellsApiException extends Exception {
    public DoorbellsApiException() {
        super();
    }

    public DoorbellsApiException(Throwable tr) {
        super(tr);
    }

    public DoorbellsApiException(String message) {
        super(message);
    }

    public DoorbellsApiException(String message, Throwable tr) {
        super(message, tr);
    }
}
