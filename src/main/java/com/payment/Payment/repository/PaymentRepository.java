package com.payment.Payment.repository;

import com.payment.Payment.entity.Payment;
import com.payment.Payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<Payment> findByIdAndUserId(Long id, String userId);

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByStatus(PaymentStatus status);
}
