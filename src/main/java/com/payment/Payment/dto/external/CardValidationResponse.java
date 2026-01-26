package com.payment.Payment.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardValidationResponse {

    private boolean valid;
    private String cardType;
    private String cardLastFour;
    private String message;
    private String validationToken;
}
