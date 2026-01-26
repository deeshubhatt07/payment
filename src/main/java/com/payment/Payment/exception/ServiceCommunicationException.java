package com.payment.Payment.exception;

public class ServiceCommunicationException extends RuntimeException {

    public ServiceCommunicationException(String message) {
        super(message);
    }

    public ServiceCommunicationException(String service, String message) {
        super(String.format("Error communicating with %s service: %s", service, message));
    }
}
