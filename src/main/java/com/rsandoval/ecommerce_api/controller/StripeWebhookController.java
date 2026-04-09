package com.rsandoval.ecommerce_api.controller;

import com.rsandoval.ecommerce_api.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final OrderService orderService;

    public StripeWebhookController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        System.out.println("========== INCOMING STRIPE WEBHOOK ==========");
        Event event;
        try {
            // Verify this actually came from Stripe
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            System.out.println("Signature verified successfully.");
        } catch (SignatureVerificationException e) {
            System.out.println("Webhook signature verification failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        // Check if the event is a successful payment
        if ("payment_intent.succeeded".equals(event.getType())) {

            event.getDataObjectDeserializer().getObject().ifPresent(stripeObject -> {
                PaymentIntent intent = (PaymentIntent) stripeObject;
                String userIdString = intent.getMetadata().get("userId");
                String addressLine = intent.getShipping().getAddress().getLine1();
                String city = intent.getShipping().getAddress().getCity();
                String zip = intent.getShipping().getAddress().getPostalCode();
                String fullAddress = addressLine + ", " + city + ", " + zip;

                if (userIdString != null) {
                    Long userId = Long.parseLong(userIdString);
                    System.out.println("Webhook received. Successful payment for User ID: " + userId);
                    orderService.placeOrderFromWebhook(userId, fullAddress);
                } else {
                    System.out.println("Webhook received, but no userId metadata was found.");
                }
            });
        }
        System.out.println("========== WEBHOOK PROCESSING COMPLETE ==========");

        // Always return a 200 OK quickly so Stripe knows we received it
        return ResponseEntity.ok("Webhook processed successfully");
    }
}
