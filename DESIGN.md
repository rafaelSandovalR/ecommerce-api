# E-Commerce API Design Doc
## 1. Architecture & Tech Stack
* **Frontend:** React(built with Vite), React Router, Context API
* **Backend:** Java Spring Boot, Spring Security (Stateless JWT Auth), Spring Data JPA
* **Database:** PostgreSQL
* **Infrastructure:** Docker containerized, deployed on Render, Github Actions for CI/CD, Playwright for E2E Testing.

## 2. Database Schema (ERD)
* **User**
  * id (Long, PK)
  * email (String, Unique)
  * name (String)
  * password (String, BCrypt Hashed)
  * role (String/Enum) -- *e.g., ROLE_USER, ROLE_ADMIN*
* **PasswordResetToken**
  * id (Long, PK)
  * token (String)
  * expiryDate(Timestamp)
  * user_id (FK) -- *One-to-One relationship*
* **Category**
  * id (Long, PK)
  * name (String, Unique)
* **Product**
  * id (Long, PK)
  * name (String)
  * description (String)
  * price (BigDecimal)
  * stock_quantity (Integer)
  * image_url (String) -- *Cloudinary URL*
  * is_deleted (Boolean) -- *For soft deletes*
  * category_id (FK) -- *Many-to-One relationship*
* **Cart**
  * id (Long, PK)
  * total_price (BigDecimal)
  * user_id (FK) -- *One-to-One relationship*
* **CartItem**
  * id (Long, PK)
  * quantity(Integer)
  * price (BigDecimal) -- *Price snapshot at tiem of addition*
  * cart_id (FK) -- *Many-to-One relationship*
* **Order**
  * id (Long, PK)
  * order_date (Timestamp)
  * status (String/Enum) -- *e.g., PAID, SHIPPED, CANCELLED*
  * total_price (BigDecimal)
  * shipping_address (String)
  * user_id (FK) -- *Many-to-One relationship*
* **OrderItem**
  * id (Long, PK)
  * quantity (Integer)
  * price (BigDecimal) -- *Locked-in purchase price*
  * order_id (FK) -- *Many-to-One relationship*
  * product_id (FK) -- *Many-to-One relationship*

## 3. API Endpoints
**Authentication & User Management** (`AuthController`)
* `POST /api/auth/register` - Register a new user
* `POST /api/auth/login` - Authenticate a user and return JWT
* `POST /api/auth/forgot-password` - Generate token and send password reset email
* `POST /api/auth/reset-password` - Consume token and update user password

**Product & Category Management** (`ProductController`, `CategoryController`)
* `GET /api/products` - List products (Supports search queries, category filtering, and pagination)
* `GET /api/products/{id}` - View Product details
* `POST /api/products` - Add a new product (Admin only, includes Cloudinary integration)
* `PUT /api/products/{id}` - Edit a product (Admin only)
* `GET /api/categories` - List all categories

**Cart System** (`CartController`)
* `GET /api/cart` - Fetch the current logged-in user's cart
* `POST /api/cart/items` - Add an item to the cart
* `PUT /api/cart/items/{itemId}` - Update the quantity of a cart item
* `DELETE /api/cart/items/{itemId}` - Remove an item from the cart
* `DELETE /api/cart` - Clear the entire cart

**Checkout & Payment** (`PaymentController`, `StripeWebhookController`)
* `POST /api/payment/create-payment-intent` - Initiate Stripe checkout securely and return client secret
* `POST /api/webhooks/stripe` - Server-to-Server endpoints. Listens for `payment_intent.succeeded` events, verifies Stripe signature, and converts the cart to an order asyncronously

**Order Management** (`OrderController`, `AdminOrderController`)
* `GET /api/orders` - View order history for the currently authenticated user
* `GET /api/admin/orders` - View all orders across the platform (Admin only)
* `PUT /api/admin/orders/{id}/status` - Update an order's fulfillment status (Admin only)