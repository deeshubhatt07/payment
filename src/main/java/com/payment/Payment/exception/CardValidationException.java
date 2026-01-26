package com.payment.Payment.exception;

public class CardValidationException extends RuntimeException {

    public CardValidationException(String message) {
        super(message);
    }
}
