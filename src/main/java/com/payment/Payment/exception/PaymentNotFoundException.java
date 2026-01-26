package com.payment.Payment.exception;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(Long paymentId) {
        super("Payment not found with id: " + paymentId);
    }
}
