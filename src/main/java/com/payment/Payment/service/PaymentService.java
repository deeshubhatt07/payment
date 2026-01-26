package com.payment.Payment.service;

import com.payment.Payment.dto.request.PaymentRequest;
import com.payment.Payment.dto.request.RefundRequest;
import com.payment.Payment.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse processPayment(String userId, String jwtToken, PaymentRequest request);

    PaymentResponse getPaymentById(Long paymentId, String userId);

    List<PaymentResponse> getUserPayments(String userId);

    PaymentResponse getPaymentByOrderId(Long orderId, String userId);

    PaymentResponse refundPayment(String userId, String jwtToken, RefundRequest request);
}
