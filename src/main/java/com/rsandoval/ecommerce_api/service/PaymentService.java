package com.rsandoval.ecommerce_api.service;

import com.rsandoval.ecommerce_api.model.Cart;
import com.rsandoval.ecommerce_api.model.User;
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
    private final AuthService authService;

    public PaymentService(CartService cartService, AuthService authService, @Value("${stripe.secret-key}") String secretKey) {
        this.cartService = cartService;
        this.authService = authService;
        Stripe.apiKey = secretKey;
    }

    @Transactional(readOnly = true)
    public PaymentIntent createPaymentIntent(String currency) throws StripeException {
        Cart cart = cartService.getCartEntity();
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        User currentUser = authService.getCurrentUser();
        BigDecimal totalAmount = cart.getTotalPrice();
        // Stripe expects amounts in cents (integers) e.g., $10.00 -> 1000
        long amountInCents = totalAmount.multiply(new BigDecimal(100)).longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency)
                .putMetadata("userId", String.valueOf(currentUser.getId())) // Nametag
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();
        return PaymentIntent.create(params);
    }
}
