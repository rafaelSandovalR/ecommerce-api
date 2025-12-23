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

---

## Setup & Installation

### Prerequisites
* Java 17+ installed
* Docker Desktop installed (for the database)

### 1. Clone the Repository
```bash

git clone https://github.com/rafaelSandovalR/ecommerce-api.git
cd ecommerce-api
```

### 2. Database Setup
This project uses a Dockerized PostgreSQL instance.
```bash

docker run --name postgres-ecommerce \
  -e POSTGRES_DB=ecommerce_db \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres
```
### 3. Configuration
Ensure your `src/main/resources/application.properties` matches your Docker settings:
```text
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=admin
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
### 4. Run the Application
```bash

./mvnw spring-boot:run
```
The API will start at `http://localhost:8080`

---
## API Endpoints
### Products
| Method   | URL                  | Description          |
|:---------|:---------------------|:---------------------|
| **GET**  | `/api/products`      | Get all products     |
| **GET**  | `/api/products/{id}` | Get product details  |
| **POST** | `/api/products`      | Create a new product |

### Shopping Cart
| Method     | URL                                   | Description            | Body (JSON)                         |
|:-----------|:--------------------------------------|:-----------------------|-------------------------------------|
| **GET**    | `/api/carts/{userId}`                 | Get user's active cart | -                                   |
| **POST**   | `/api/carts/{userId}/add`             | Add item to cart       | `{ "productId": 1, "quantity": 2 }` |
| **PUT**    | `/api/carts/{userId}/items`           | Update item quantity   | `{ "productId": 1, "quantity": 5 }` |
| **DELETE** | `/api/carts/{userId}/remove/{itemId}` | Remove specific item   | -                                   |
| **DELETE** | `/api/carts/{userId}/clear`           | Clear entire cart      | -                                   |

### Orders
| Method   | URL                          | Description                      |
|:---------|:-----------------------------|:---------------------------------|
| **POST** | `/api/orders/{userId}/place` | Checkout (Convert Cart -> Order) |
| **GET**  | `/api/orders/user/{userId}`  | Get order history for user       |
| **GET**  | `/api/orders/{orderId}`      | Get Receipt for specific order   |