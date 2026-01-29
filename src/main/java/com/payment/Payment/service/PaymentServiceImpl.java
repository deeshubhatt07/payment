package com.payment.Payment.service;

import com.payment.Payment.dto.external.CardValidationResponse;
import com.payment.Payment.dto.request.PaymentRequest;
import com.payment.Payment.dto.request.RefundRequest;
import com.payment.Payment.dto.response.PaymentResponse;
import com.payment.Payment.entity.Payment;
import com.payment.Payment.entity.PaymentStatus;
import com.payment.Payment.exception.CardValidationException;
import com.payment.Payment.exception.OrderNotFoundException;
import com.payment.Payment.exception.PaymentNotFoundException;
import com.payment.Payment.exception.PaymentProcessingException;
import com.payment.Payment.exception.RefundException;
import com.payment.Payment.exception.ServiceCommunicationException;
import com.payment.Payment.repository.PaymentRepository;
import com.payment.Payment.util.PaymentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CardServiceClient cardServiceClient;
    private final OrderServiceClient orderServiceClient;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse processPayment(String userId, String jwtToken, PaymentRequest request) {
        log.info("Processing payment for user {} on order {}", userId, request.getOrderId());

        // Verify order exists in Order service
        try {
            orderServiceClient.getOrder(request.getOrderId(), jwtToken);
        } catch (ServiceCommunicationException e) {
            log.error("Order {} not found or order service unavailable: {}", request.getOrderId(), e.getMessage());
            throw new OrderNotFoundException(request.getOrderId());
        }

        // Check if payment already exists for this order
        paymentRepository.findByOrderId(request.getOrderId())
                .ifPresent(existing -> {
                    if (existing.getStatus() == PaymentStatus.COMPLETED) {
                        throw new PaymentProcessingException("Payment already completed for this order");
                    }
                });

        // Validate card with Card service
        CardValidationResponse cardValidation;
        try {
            cardValidation = cardServiceClient.validateCard(request.getCardDetails(), jwtToken);
        } catch (Exception e) {
            log.error("Card validation failed: {}", e.getMessage());
            throw new CardValidationException("Card validation service unavailable");
        }

        if (!cardValidation.isValid()) {
            log.warn("Card validation failed: {}", cardValidation.getMessage());
            throw new CardValidationException("Card validation failed: " + cardValidation.getMessage());
        }

        // Create payment record
        Payment payment = Payment.builder()
                .userId(userId)
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(PaymentStatus.PROCESSING)
                .cardLastFour(cardValidation.getCardLastFour())
                .cardType(cardValidation.getCardType())
                .transactionId(generateTransactionId())
                .build();

        payment = paymentRepository.save(payment);

        // Simulate payment processing
        try {
            // In production, integrate with actual payment gateway
            simulatePaymentProcessing();

            payment.setStatus(PaymentStatus.COMPLETED);
            payment = paymentRepository.save(payment);

            // Update order status
            orderServiceClient.updateOrderStatus(request.getOrderId(), "PROCESSING", jwtToken);

            log.info("Payment {} completed successfully for order {}",
                    payment.getTransactionId(), request.getOrderId());

        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);
            throw new PaymentProcessingException("Payment processing failed: " + e.getMessage());
        }

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId, String userId) {
        Payment payment = paymentRepository.findByIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getUserPayments(String userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId, String userId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .filter(p -> p.getUserId().equals(userId))
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + orderId));
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse refundPayment(String userId, String jwtToken, RefundRequest request) {
        log.info("Processing refund for payment {} by user {}", request.getPaymentId(), userId);

        Payment payment = paymentRepository.findByIdAndUserId(request.getPaymentId(), userId)
                .orElseThrow(() -> new PaymentNotFoundException(request.getPaymentId()));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RefundException("Can only refund completed payments. Current status: " + payment.getStatus());
        }

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new RefundException("Payment has already been refunded");
        }

        // Simulate refund processing
        try {
            simulateRefundProcessing();

            payment.setStatus(PaymentStatus.REFUNDED);
            payment = paymentRepository.save(payment);

            // Update order status
            orderServiceClient.updateOrderStatus(payment.getOrderId(), "CANCELLED", jwtToken);

            log.info("Refund completed for payment {}", payment.getTransactionId());

        } catch (Exception e) {
            throw new RefundException("Refund processing failed: " + e.getMessage());
        }

        return paymentMapper.toResponse(payment);
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void simulatePaymentProcessing() {
        // Simulate payment gateway processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void simulateRefundProcessing() {
        // Simulate refund processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
