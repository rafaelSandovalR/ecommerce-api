package com.rsandoval.ecommerce_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ecommerceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Commerce API")
                        .description("Backend REST API for a full-stack e-commerce platform. Handles users, products, carts, and Stripe webhooks.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Rafael Sandoval")
                                .url("https://github.com/rafaelSandovalR")));
    }
}
