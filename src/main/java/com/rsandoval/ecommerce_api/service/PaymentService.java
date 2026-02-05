package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.model.Cart;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final CartService cartService;

    public PaymentService(CartService cartService, @Value("${STRIPE_SECRET_KEY}") String secretKey) {
        this.cartService = cartService;
        Stripe.apiKey = secretKey;
    }

    @Transactional(readOnly = true)
    public PaymentIntent createPaymentIntent(String currency) throws StripeException {
        Cart cart = cartService.getCartEntity();
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        BigDecimal totalAmount = cart.getTotalPrice();

        // Stripe expects amounts in cents (integers) e.g., $10.00 -> 1000
        long amountInCents = totalAmount.multiply(new BigDecimal(100)).longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();
        return PaymentIntent.create(params);
    }
}
