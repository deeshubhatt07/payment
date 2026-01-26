package com.payment.Payment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {

    @NotNull(message = "Payment ID is required")
    private Long paymentId;

    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    private BigDecimal amount; // Optional - if null, full refund

    private String reason;
}
