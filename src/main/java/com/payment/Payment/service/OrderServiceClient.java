package com.payment.Payment.service;

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
public class OrderServiceClient {

    private final RestClient orderServiceRestClient;

    public boolean updateOrderStatus(Long orderId, String status, String jwtToken) {
        log.debug("Updating order {} status to {}", orderId, status);

        try {
            // This would call an endpoint to update order status
            // For now, we'll log it - in production, implement actual API call
            log.info("Order {} status would be updated to {}", orderId, status);
            return true;
        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage());
            return false;
        }
    }

    public Map<String, Object> getOrder(Long orderId, String jwtToken) {
        log.debug("Fetching order {}", orderId);

        try {
            return orderServiceRestClient.get()
                    .uri("/api/orders/{id}", orderId)
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            log.error("Error fetching order: {}", e.getMessage());
            throw new ServiceCommunicationException("Order", e.getMessage());
        }
    }
}
