package com.rsandoval.ecommerce_api.controller;

import com.rsandoval.ecommerce_api.service.OrderService;
import com.stripe.exception.EventDataObjectDeserializationException;
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
            @RequestHeader("Stripe-Signature") String sigHeader) throws EventDataObjectDeserializationException {

        System.out.println("========== INCOMING STRIPE WEBHOOK ==========");

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            System.out.println("Signature verified successfully.");
        } catch (SignatureVerificationException e) {
            System.out.println("Webhook signature verification failed.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        System.out.println("Event Type: " + event.getType());

        if ("payment_intent.succeeded".equals(event.getType())) {

            PaymentIntent intent;

            // Handle the API version mismatch gracefully
            if (event.getDataObjectDeserializer().getObject().isPresent()) {
                intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
                System.out.println("Object deserialized safely.");
            } else {
                System.out.println("Version mismatch detected! Falling back to unsafe deserialization.");
                intent = (PaymentIntent) event.getDataObjectDeserializer().deserializeUnsafe();
            }

            String userIdString = intent.getMetadata().get("userId");

            if (userIdString != null) {
                Long userId = Long.parseLong(userIdString);
                System.out.println("Success! Extracted User ID: " + userId);

                try {
                    orderService.placeOrderFromWebhook(userId, "Address pending...");
                    System.out.println("Order successfully placed in database.");
                } catch (Exception e) {
                    // If the database fails (e.g. user not found), we will see it here!
                    System.out.println("CRITICAL ERROR placing order: " + e.getMessage());
                }
            } else {
                System.out.println("Webhook received, but NO userId metadata was found on the PaymentIntent.");
            }
        }

        System.out.println("========== WEBHOOK PROCESSING COMPLETE ==========");
        return ResponseEntity.ok("Webhook processed successfully");
    }
}
