# Full-Stack E-Commerce Platform
A Full-Stack E-Commerce application built with a React frontend, Java Spring Boot backend, and PostgreSQL.

This flagship portfolio project demonstrates real-world architectural patterns,
including stateless JWT authentication, asynchronous payment processing via Stripe Webhooks, role-based access control (RBAC), and containerized CI/CD deployment.

## Live Demo
Experience the fully functional, cloud-hosted platform here:
**[Live E-Commerce App](https://ecommerce-frontend-6vgo.onrender.com/)**

---
## Features
* **Role-Based Access Control (RBAC)**: Secure, stateless JWT authentication differentiating between standard Shoppers and Administrators. Includes a secure "magic link" password reset flow via email. 
* **Admin Dashboard**: A protected portal for inventory management. Admins can create/edit products, upload images directly to Cloudinary, and update order fulfillment statuses.
* **Product Catalog**: Dynamic catalog featuring search queries, category filtering, and pagination.
* **Database-Backed Cart System**: Persistent shopping carts that automatically synchronize with the React frontend and prevent overselling.
* **Asynchronous Checkout & Webhooks**: Integrated Stripe using a Server-to-Server webhooks architecture to ensure strict data integrity and prevent lost orders upon client disconnects.

## Architecture & Challenges Overcome
* **The Webhook Race Condition**: Bypassed the common flaw of relying on a React frontend to confirm payments. Created a dedicated Spring Boot webhook controller that securely listens for Stripe events (`payment_intent.succeeded`) and builds the order in the background. Implemented a 10-second pollling mechanism on the frontend to wait for the backend to clear the database cart, preventing "ghost cart" UI bugs.
* **Global Exception Handling**: Centralized error management using Spring's `@ControllerAdvice` to intercept exceptions (e.g., expired JWTs, invalid tokens) and return clean, standardized JSON responses to the frontend.

## Tech Stack
* **Frontend**: React (Vite), React Router, Context API, Tailwind CSS
* **Backend**: Java 17, Spring Boot 3, Spring Security, Spring Data JPA (Hibernate)
* **Database**: PostgreSQL
* **Third-Party Integrations**: Stripe (Payments), Cloudinary (Images), Spring Mail/SMTP (Password Resets)
* **DevOps & Testing**: Docker, Docker Compose, Render, Github Actions (CI/CD), Playwright (E2E Testing), JUnit

---
## Setup & Installation

### Prerequisites
* Java 17+ installed
* Node.js & npm installed (for the frontend)
* Docker Desktop installed
* Stripe CLI installed (for local webhook testing)

### 1. Clone the Repository
```bash
git clone https://github.com/rafaelSandovalR/ecommerce-api.git
cd ecommerce-api
```

### 2. Backend Configuration & Secrets
This application integrates with several third-party services. To run the full checkout, image upload, and password reset flows locally, you will need free developer accounts for Stripe, Cloudinary, and a Google account (for SMTP).

Create a file named `secrets.properties` inside `src/main/resources/` (this file is `.gitignored` by default) and provide your own API keys:

```properties
# JWT Security
# Generate a random string of 64 characters
JWT_SECRET=your_super_secret_key_that_is_sixty_four_characters_long_1234567

# Cloudinary (Image Hosting)
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Stripe (Payments)
STRIPE_SECRET_KEY=sk_test_your_stripe_secret_key
# For local webhook testing, use the secret provided by the Stripe CLI
# WILL BE OBTAINED IN THE FOLLOWING STEP
STRIPE_WEBHOOK_SECRET=whsec_your_local_cli_secret

# Email Setup (For Password Resets)
MAIL_USERNAME=your_email@gmail.com
# Use a 16-character Google App Password, not your standard password
MAIL_PASSWORD=your_16_character_app_password
```

### 3. Run the Application

### Option A: Local Development Setup (Recommended)

**Terminal 1 (Database):**
```bash
docker-compose up ecommerce-db -d
```
**Terminal 2 (Backend):**
```bash
./mvnw spring-boot:run
```
**Terminal 3 (Frontend):**
```bash
cd frontend
npm install
npm run dev
```

**Terminal 4 (Stripe Webhook Tunnel for Local Checkout):**
```bash
stripe listen --forward-to localhost:8080/api/webhooks/stripe
```
*(Copy the generated whsec_... secret and place it in your backend properties to successfully test local payments).*

### Option B: Full Docker Orchestration (Production Simulation)
If you want to run the fully containerized production stack rather than using Vite's development server, you can orchestrate the entire application with a single command.

This option utilizes multi-stage Dockerfiles to compile the React frontend into static HTML/JS files served by Nginx, and packages the Spring Boot backend into a lightweight Java JRE container.

*Note: Ensure you have created your `secrets.properties` file inside `src/main/resources/` (as outlined in Step 2) before running this command, as the Docker build process will compile those keys into the final `.jar` file.*

**1. Build and start the entire stack:**

```bash
docker-compose up --build -d
```

**2. Access the Application:**

Frontend: http://localhost:5173 (Served via Nginx)
Backend API: http://localhost:8080

**3. Stripe Webhooks (Required for local checkout)**

Even though the Spring Boot backend is running inside an isolated Docker network, port 8080 is mapped to your host machine, you still need to route Stripe events into the container using the Stripe CLI: 
```bash
stripe listen --forward-to localhost:8080/api/webhooks/stripe
```
*(To stop the application and database, simply run docker-compose down)*

---
## Core API Endpoints
### Authentication & Users
| Method   | URL                         | Description                         |
|:---------|:----------------------------|:------------------------------------|
| **POST** | `/api/auth/register`        | Register a new user                 |
| **POST** | `/api/auth/login`           | Authenticate user and return JWT    |
| **POST** | `/api/auth/forgot-password` | Generate token and send reset email |
| **POST** | `/api/auth/reset-password`  | Consume token and update password   |

### Products & Categories
| Method     | URL                  | Description                                         |
|:-----------|:---------------------|:----------------------------------------------------|
| **GET**    | `/api/products`      | Get products (Supports search, filters, pagination) |
| **GET**    | `/api/products/{id}` | Get products details                                |
| **POST**   | `/api/products`      | Create a new product (Admin Only)                   |
| **GET**    | `/api/categories`    | List all categories                                 |

### Secure Cart System (Requires JWT)
| Method     | URL                          | Description             |
|:-----------|:-----------------------------|:------------------------|
| **GET**    | `/api/carts`                 | Get current user's cart |
| **POST**   | `/api/carts/add`             | Add item to cart        |
| **PUT**    | `/api/carts/items`           | Update item quantity    |
| **DELETE** | `/api/carts/remove/{itemId}` | Remove specific item    |
| **DELETE** | `/api/carts/clear`           | Clear entire cart       |

### Orders & Payments
| Method   | URL                                  | Description                            |
|:---------|:-------------------------------------|:---------------------------------------|
| **POST** | `/api/payment/create-payment-intent` | Initiate Stripe checkout securely      |
| **POST** | `/api/webhooks/stripe`               | Async webhook: Converts Cart -> Order  |
| **GET**  | `/api/orders`                        | Get order history for the current user |
| **GET**  | `/api/admin/orders`                  | View platform-wide orders (Admin Only) |
| **PUT**  | `/api/admin/orders/{id}/status`      | Update fulfillment status (Admin Only) |
