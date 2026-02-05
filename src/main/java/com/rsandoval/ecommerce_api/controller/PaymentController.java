package com.rsandoval.ecommerce_api.controller;

import com.rsandoval.ecommerce_api.dto.payment.PaymentResponse;
import com.rsandoval.ecommerce_api.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-payment-intent")
    public ResponseEntity<PaymentResponse> createPaymentIntent() throws StripeException {
        PaymentIntent paymentIntent = paymentService.createPaymentIntent("usd");
        return ResponseEntity.ok(new PaymentResponse(paymentIntent.getClientSecret()));
    }
}
