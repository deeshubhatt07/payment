package com.payment.Payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${card.service.base-url}")
    private String cardServiceBaseUrl;

    @Value("${order.service.base-url}")
    private String orderServiceBaseUrl;

    @Bean
    public RestClient cardServiceRestClient() {
        return RestClient.builder()
                .baseUrl(cardServiceBaseUrl)
                .build();
    }

    @Bean
    public RestClient orderServiceRestClient() {
        return RestClient.builder()
                .baseUrl(orderServiceBaseUrl)
                .build();
    }
}
