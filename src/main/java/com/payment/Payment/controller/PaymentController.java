package com.payment.Payment.controller;

import com.payment.Payment.dto.ErrorResponse;
import com.payment.Payment.dto.request.PaymentRequest;
import com.payment.Payment.dto.request.RefundRequest;
import com.payment.Payment.dto.response.PaymentResponse;
import com.payment.Payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payments", description = "Payment processing APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Process a payment", description = "Validates card and processes payment for an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment processed successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or card validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Payment processing failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        String jwtToken = (String) authentication.getCredentials();
        log.info("Payment request from user {} for order {}", userId, request.getOrderId());

        PaymentResponse response = paymentService.processPayment(userId, jwtToken, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get user's payments", description = "Retrieves all payments for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<PaymentResponse>> getUserPayments(Authentication authentication) {
        String userId = authentication.getName();
        log.debug("Fetching payments for user {}", userId);

        List<PaymentResponse> payments = paymentService.getUserPayments(userId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves a specific payment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long id,
            Authentication authentication) {
        String userId = authentication.getName();
        log.debug("Fetching payment {} for user {}", id, userId);

        PaymentResponse payment = paymentService.getPaymentById(id, userId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order ID", description = "Retrieves payment for a specific order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @PathVariable Long orderId,
            Authentication authentication) {
        String userId = authentication.getName();
        log.debug("Fetching payment for order {} for user {}", orderId, userId);

        PaymentResponse payment = paymentService.getPaymentByOrderId(orderId, userId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/refund")
    @Operation(summary = "Refund a payment", description = "Processes a refund for a completed payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refund processed successfully",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid refund request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PaymentResponse> refundPayment(
            @Valid @RequestBody RefundRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        String jwtToken = (String) authentication.getCredentials();
        log.info("Refund request from user {} for payment {}", userId, request.getPaymentId());

        PaymentResponse response = paymentService.refundPayment(userId, jwtToken, request);
        return ResponseEntity.ok(response);
    }
}
