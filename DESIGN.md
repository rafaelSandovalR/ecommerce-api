# E-Commerce API Design Doc

## 1. Database Schema (ERD)
* **Category**
  * id (Long, PK)
  * name (String)
* **Product**
  * id (Long, PK)
  * name (String)
  * description (String)
  * price (BigDecimal)
  * stock_quantity (Integer)
  * category_id (FK) -- *Many-to-One relationship*

## 2. API Endpoints (Phase 1)
* `GET /api/products` - List all products
* `GET /api/products/{id}` - View details
* `POST /api/products` - Add new product (Admin only)
* `GET /api/categories` - List categories