# Java Spring Boot E-Commerce API
A robust , production-grade RESTful API for an E-commerce platform built with Java, Spring Boot, and PostgreSQL.

This backend handles the core logic for product management, shopping carts, and order processing with strict transactional integrity.

## Features
* **Product Catalog**: Managed inventory with Categories and stock tracking.
* **Shopping Cart**:
  * Add/Update/Remove items.
  * Dynamic total calculation using `BigDecimal` for financial precision.
  * Stock validation (prevents overselling).
* **OrderSystem**:
  * Transactional "Checkout" process (Atomic operations).
  * Automatically reduce inventory and archives item prices at the time of purchase.
  * Order history tracking.
* **Architecture**:
  * **DTO Pattern**: Separation of concerns between Internal Entities and External API.
  * **Service Layer**: Business logic decoupled from Controllers.
  * **Global Exception Handling**: Centralized error management with clean JSON responses.

## Tech Stack
* **Language**: Java 17
* **Framework**: Spring Boot 3
* **Database**: PostgreSQL (Dockerized)
* **Persistence**: Spring Data JPA (Hibernate)
* **Validation**: Hibernate Validator
* **Tools**: Lombok, Docker, Postman