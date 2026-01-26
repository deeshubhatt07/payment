package com.payment.Payment.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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
public class PaymentRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotNull(message = "Card details are required")
    @Valid
    private CardDetails cardDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardDetails {
        @NotBlank(message = "Card number is required")
        private String cardNumber;

        @NotBlank(message = "Card holder name is required")
        private String cardHolderName;

        @NotBlank(message = "Expiry month is required")
        private String expiryMonth;

        @NotBlank(message = "Expiry year is required")
        private String expiryYear;

        @NotBlank(message = "CVV is required")
        private String cvv;
    }
}
