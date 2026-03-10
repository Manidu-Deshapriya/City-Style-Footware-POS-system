# CityStyle Footwear - Database ER Diagram

This document provides a visual and symbolic representation of the database schema for the CityStyle Footwear POS system.

## 1. Mermaid Diagram (Visual)

```mermaid
erDiagram
    CATEGORIES ||--o{ SHOES : "categorizes"
    BRANDS ||--o{ SHOES : "brands"
    SUPPLIERS ||--o{ SHOES : "supplies"
    CUSTOMERS ||--o{ SALES : "places"
    USERS ||--o{ SALES : "records"
    SALES ||--|{ SALES_ITEMS : "includes"
    SHOES ||--o{ SALES_ITEMS : "ordered_in"
    SALES_ITEMS ||--o| RETURNS : "result_in"
    USERS ||--o{ RETURNS : "authorizes"

    CATEGORIES {
        int category_id PK
        varchar name
    }
    BRANDS {
        int brand_id PK
        varchar name
    }
    SUPPLIERS {
        int supplier_id PK
        varchar name
        varchar contact_person
        varchar phone
        varchar email
    }
    CUSTOMERS {
        int customer_id PK
        varchar name
        varchar email
        varchar phone
    }
    USERS {
        int user_id PK
        varchar username
        varchar password_hash
        varchar role
    }
    SHOES {
        int shoe_id PK
        varchar model_name
        int brand_id FK
        int category_id FK
        varchar size
        varchar color
        int stock_quantity
        int reorder_threshold
        decimal base_price
        decimal promotional_price
        int supplier_id FK
    }
    SALES {
        int sale_id PK
        int customer_id FK
        int user_id FK
        timestamp sale_date
        decimal total_amount
        decimal discount
    }
    SALES_ITEMS {
        int sale_item_id PK
        int sale_id FK
        int shoe_id FK
        int quantity
        decimal unit_price
    }
    RETURNS {
        int return_id PK
        int sale_item_id FK
        text reason
        varchar status
        int approved_by FK
        timestamp return_date
    }
```

## 2. Symbolic Representation (Text-Based)

Legend:
- `[Table Name]` : Entity
- `pk` : Primary Key
- `fk` : Foreign Key
- `--<` : One-to-Many relationship
- `---` : One-to-One relationship

```text
[BRANDS] (pk: brand_id)
   |
   +--< [SHOES] (pk: shoe_id, fk: brand_id, category_id, supplier_id)
   |       |
[CATEGORIES] (pk: category_id)
           |
[SUPPLIERS] (pk: supplier_id)

[SHOES]
   |
   +--< [SALES_ITEMS] (pk: sale_item_id, fk: sale_id, shoe_id)
           |
[SALES] (pk: sale_id, fk: customer_id, user_id)
   |       |
   |       +--< [SALES_ITEMS]
   |
[CUSTOMERS] (pk: customer_id)
   |
[USERS] (pk: user_id)
   |
   +--< [SALES] (Processed by)
   |
   +--< [RETURNS] (Approved by)

[SALES_ITEMS]
   |
   +---o [RETURNS] (pk: return_id, fk: sale_item_id, approved_by)
```

## 3. Detailed Table Relationships

| Table A | Table B | Relationship | Description |
| :--- | :--- | :--- | :--- |
| `categories` | `shoes` | 1 : N | One category can have many shoes. |
| `brands` | `shoes` | 1 : N | One brand can have many shoe models. |
| `suppliers` | `shoes` | 1 : N | One supplier can provide multiple shoe products. |
| `customers` | `sales` | 1 : N | One customer can make multiple purchases. |
| `users` | `sales` | 1 : N | One staff member (user) can process many sales. |
| `sales` | `sales_items` | 1 : N | One sale can include multiple shoe items. |
| `shoes` | `sales_items` | 1 : N | One shoe product can be part of many sale transactions. |
| `sales_items` | `returns` | 1 : 0..1 | A sold item may or may not be returned. |
| `users` | `returns` | 1 : N | A manager (user) can approve many returns. |
