package com.payment.Payment.service;

import com.payment.Payment.dto.external.CardValidationResponse;
import com.payment.Payment.dto.request.PaymentRequest;
import com.payment.Payment.exception.ServiceCommunicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceClient {

    private final RestClient cardServiceRestClient;

    public CardValidationResponse validateCard(PaymentRequest.CardDetails cardDetails, String jwtToken) {
        log.debug("Validating card with Card service");

        try {
            Map<String, String> request = Map.of(
                    "cardNumber", cardDetails.getCardNumber(),
                    "cardHolderName", cardDetails.getCardHolderName(),
                    "expiryMonth", cardDetails.getExpiryMonth(),
                    "expiryYear", cardDetails.getExpiryYear(),
                    "cvv", cardDetails.getCvv()
            );

            return cardServiceRestClient.post()
                    .uri("/api/cards/validate")
                    .header("Authorization", "Bearer " + jwtToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(CardValidationResponse.class);
        } catch (Exception e) {
            log.error("Error validating card: {}", e.getMessage());
            throw new ServiceCommunicationException("Card", e.getMessage());
        }
    }
}
