-- Database Setup for CityStyle Footwear POS
-- Version 2.0 (Enhanced Data)

-- 1. DROP TABLES (Reverse Order of Dependencies)
DROP TABLE IF EXISTS returns CASCADE;
DROP TABLE IF EXISTS sales_items CASCADE;
DROP TABLE IF EXISTS sales CASCADE;
DROP TABLE IF EXISTS shoes CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS suppliers CASCADE;
DROP TABLE IF EXISTS brands CASCADE;
DROP TABLE IF EXISTS categories CASCADE;

-- 2. CREATE TABLES

CREATE TABLE categories (
    category_id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE brands (
    brand_id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE suppliers (
    supplier_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE customers (
    customer_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20)
);

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('Manager', 'Cashier'))
);

CREATE TABLE shoes (
    shoe_id SERIAL PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    brand_id INTEGER REFERENCES brands(brand_id),
    category_id INTEGER REFERENCES categories(category_id),
    size VARCHAR(30) NOT NULL, -- Display size range e.g. "7 - 12"
    min_size INTEGER DEFAULT 0,
    max_size INTEGER DEFAULT 0,
    color VARCHAR(20) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    reorder_threshold INTEGER DEFAULT 5,
    base_price DECIMAL(10,2) NOT NULL,
    promotional_price DECIMAL(10,2),
    supplier_id INTEGER REFERENCES suppliers(supplier_id)
);

CREATE TABLE sales (
    sale_id SERIAL PRIMARY KEY,
    customer_id INTEGER REFERENCES customers(customer_id),
    user_id INTEGER REFERENCES users(user_id),
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    discount DECIMAL(10,2) DEFAULT 0.00
);

CREATE TABLE sales_items (
    sale_item_id SERIAL PRIMARY KEY,
    sale_id INTEGER REFERENCES sales(sale_id),
    shoe_id INTEGER REFERENCES shoes(shoe_id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    selected_size VARCHAR(20)
);

CREATE TABLE returns (
    return_id SERIAL PRIMARY KEY,
    sale_item_id INTEGER REFERENCES sales_items(sale_item_id),
    reason TEXT,
    status VARCHAR(20) DEFAULT 'Pending' CHECK (status IN ('Pending', 'Approved', 'Rejected')),
    approved_by INTEGER REFERENCES users(user_id),
    return_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. INSERT DATA

-- Categories
INSERT INTO categories (name) VALUES
('Sneakers'),
('Loafers'),
('Heels'),
('Sandals'),
('Boots'),
('Kids'),
('Formal Shoes'),
('Sports Shoes'),
('Slippers'),
('Flip Flops'),
('School Shoes'),
('Canvas'),
('Wedges'),
('Moccasins'),
('Oxford'),
('Derby'),
('Hiking'),
('Ballet Flats');

-- Brands
INSERT INTO brands (name) VALUES
('Nike'),
('Adidas'),
('Clarks'),
('Gucci'),
('Puma'),
('Reebok'),
('Bata'),
('Skechers'),
('New Balance'),
('Under Armour'),
('Crocs'),
('Converse'),
('Vans'),
('Fila'),
('Asics'),
('Jordan'),
('Timberland'),
('Dr. Martens'),
('Aldo'),
('Hush Puppies');

-- Suppliers
INSERT INTO suppliers (name, contact_person, phone, email) VALUES
('City Warehouse', 'John Doe', '555-0199', 'city@warehouse.com'),
('Metro Footwear Suppliers', 'Alice Brown', '555-0201', 'metro@footwear.com'),
('Global Shoes Pvt Ltd', 'Michael Lee', '555-0205', 'global@shoes.com'),
('Urban Steps Distributors', 'Saman Perera', '077-3456789', 'urban@steps.lk'),
('Kids Footwear Lanka', 'Nimali Silva', '071-9988776', 'kids@footwear.lk'),
('Lanka Leather Works', 'Kamal Gunaratne', '077-1122334', 'llw@lanka.lk'),
('Fashion Import Corp', 'Sarah Jones', '011-2233445', 'imports@fashion.lk'),
('Sporty Traders', 'Ravi De Silva', '076-5566778', 'ravi@sporty.lk');

-- Users
INSERT INTO users (username, password_hash, role) VALUES
('admin', 'admin123', 'Manager'),
('manidu', 'manidu123', 'Manager'),
('cashier', 'cashier123', 'Cashier'),
('kasun', 'kasun123', 'Cashier'),
('123', '123', 'Manager');

-- Customers
INSERT INTO customers (name, phone) VALUES
('Nimal Perera', '0771234567'),
('Saman Silva', '0719876543'),
('Kamani Rajapakshe', '0761239876'),
('Ruwan Dissanaike', '0775556666');

-- Shoes (Extensive List)
-- Shoes (Curated List (~20 items) with Size/Color Variations)
-- Note: Size is now specific per row to allow separate quantities

-- Nike Air Force 1 (Individual Sizes)
INSERT INTO shoes (model_name, brand_id, category_id, size, min_size, max_size, color, stock_quantity, base_price, promotional_price, supplier_id) VALUES
-- White (15 Total -> 5 each)
('Air Force 1', 1, 1, '8', 8, 8, 'White', 5, 42000.00, 41000.00, 2),
('Air Force 1', 1, 1, '10', 10, 10, 'White', 5, 42000.00, 41000.00, 2),

-- Black (15 Total -> 5 each)
('Air Force 1', 1, 1, '8', 8, 8, 'Black', 5, 42000.00, 40000.00, 2),
('Air Force 1', 1, 1, '10', 10, 10, 'Black', 5, 42000.00, 40000.00, 2),

-- White/Blue (12 Total -> 4 each)
('Air Force 1', 1, 1, '8', 8, 8, 'White/Blue', 4, 42500.00, 40500.00, 2),
('Air Force 1', 1, 1, '9', 9, 9, 'White/Blue', 4, 42500.00, 40500.00, 2),
('Air Force 1', 1, 1, '10', 10, 10, 'White/Blue', 4, 42500.00, 40500.00, 2),

-- Triple Red (10 Total -> 4, 3, 3)
('Air Force 1', 1, 1, '8', 8, 8, 'Triple Red', 4, 43000.00, 42000.00, 2),
('Air Force 1', 1, 1, '10', 10, 10, 'Triple Red', 3, 43000.00, 42000.00, 2),

-- Grey/Black (10 Total -> 4, 3, 3)
('Air Force 1', 1, 1, '8', 8, 8, 'Grey/Black', 4, 41500.00, 39500.00, 2),
('Air Force 1', 1, 1, '9', 9, 9, 'Grey/Black', 3, 41500.00, 39500.00, 2);

-- Adidas Superstar (Individual Sizes)
INSERT INTO shoes (model_name, brand_id, category_id, size, min_size, max_size, color, stock_quantity, base_price, promotional_price, supplier_id) VALUES
-- White/Black (18 Total -> 6 each for 7,8,9)
('Superstar', 2, 1, '7', 7, 7, 'White/Black', 6, 35000.00, 32000.00, 2),
('Superstar', 2, 1, '8', 8, 8, 'White/Black', 6, 35000.00, 32000.00, 2),
('Superstar', 2, 1, '9', 9, 9, 'White/Black', 6, 35000.00, 32000.00, 2),

-- Core Black (15 Total -> 5 each)
('Superstar', 2, 1, '8', 8, 8, 'Core Black', 5, 35000.00, 32500.00, 2),
('Superstar', 2, 1, '9', 9, 9, 'Core Black', 5, 35000.00, 32500.00, 2),

-- Cloud White (12 Total -> 4 each)
('Superstar', 2, 1, '8', 8, 8, 'Cloud White', 4, 34500.00, 31500.00, 2),
('Superstar', 2, 1, '9', 9, 9, 'Cloud White', 4, 34500.00, 31500.00, 2),
('Superstar', 2, 1, '10', 10, 10, 'Cloud White', 4, 34500.00, 31500.00, 2),

-- White/Gold (10 Total -> 4, 3, 3)
('Superstar', 2, 1, '8', 8, 8, 'White/Gold', 4, 36000.00, 33000.00, 2),
('Superstar', 2, 1, '10', 10, 10, 'White/Gold', 3, 36000.00, 33000.00, 2);

-- Formal / Office Options (Individual Sizes for Stock Tracking - Adults)
INSERT INTO shoes (model_name, brand_id, category_id, size, min_size, max_size, color, stock_quantity, base_price, promotional_price, supplier_id) VALUES
-- Oxford Classic - Matte Black (15 Total -> 5 each)
('Oxford Classic', 3, 7, '6', 6, 6, 'Matte Black', 5, 35000.00, 34000.00, 6),
('Oxford Classic', 3, 7, '8', 8, 8, 'Matte Black', 5, 35000.00, 34000.00, 6),

-- Oxford Classic - Dark Brown (15 Total -> 5 each)
('Oxford Classic', 3, 7, '6', 6, 6, 'Dark Brown', 5, 35000.00, 33500.00, 6),
('Oxford Classic', 3, 7, '7', 7, 7, 'Dark Brown', 5, 35000.00, 33500.00, 6),

-- Oxford Classic - Burgundy (10 Total -> 4, 3, 3)
('Oxford Classic', 3, 7, '6', 6, 6, 'Burgundy', 4, 34000.00, 33000.00, 6),
('Oxford Classic', 3, 7, '7', 7, 7, 'Burgundy', 3, 34000.00, 33000.00, 6),
('Oxford Classic', 3, 7, '8', 8, 8, 'Burgundy', 3, 34000.00, 33000.00, 6),

-- Derby Plain (12 Total -> 4 each)
('Derby Plain', 3, 16, '6', 6, 6, 'Chocolate Brown', 4, 31500.00, 30000.00, 6),
('Derby Plain', 3, 16, '8', 8, 8, 'Chocolate Brown', 4, 31500.00, 30000.00, 6);


-- Ladies Heels (Individual Sizes - Using EU Sizing 36-40 for clarity)
INSERT INTO shoes (model_name, brand_id, category_id, size, min_size, max_size, color, stock_quantity, base_price, promotional_price, supplier_id) VALUES
-- Stiletto Pump - Red Velvet
('Stiletto Pump', 19, 3, '36', 36, 36, 'Red Velvet', 4, 22500.00, 21000.00, 7),
('Stiletto Pump', 19, 3, '37', 37, 37, 'Red Velvet', 4, 22500.00, 21000.00, 7),

-- Stiletto Pump - Metallic Silver
('Stiletto Pump', 19, 3, '36', 36, 36, 'Metallic Silver', 4, 22500.00, 21500.00, 7),
('Stiletto Pump', 19, 3, '38', 38, 38, 'Metallic Silver', 3, 22500.00, 21500.00, 7),

-- Stiletto Pump - Glossy Black
('Stiletto Pump', 19, 3, '36', 36, 36, 'Glossy Black', 5, 22000.00, 20500.00, 7),
('Stiletto Pump', 19, 3, '37', 37, 37, 'Glossy Black', 5, 22000.00, 20500.00, 7),

-- Block Heel
('Block Heel', 3, 3, '36', 36, 36, 'Beige Nude', 4, 25000.00, 23500.00, 6),
('Block Heel', 3, 3, '37', 37, 37, 'Beige Nude', 3, 25000.00, 23500.00, 6),
('Block Heel', 3, 3, '38', 38, 38, 'Beige Nude', 3, 25000.00, 23500.00, 6);


-- Kids Options (Individual Sizes - Using Kids Sizing 10-13)
INSERT INTO shoes (model_name, brand_id, category_id, size, min_size, max_size, color, stock_quantity, base_price, promotional_price, supplier_id) VALUES
-- Kids Light Up Sneakers
('Zoom Kids', 11, 6, '10', 10, 10, 'Blue/Red', 5, 8500.00, 8000.00, 5),
('Zoom Kids', 11, 6, '11', 11, 11, 'Blue/Red', 5, 8500.00, 8000.00, 5),

-- Kids School Shoes
('School Classic', 12, 11, '10', 10, 10, 'Black', 10, 4500.00, 4200.00, 1),
('School Classic', 12, 11, '12', 12, 12, 'Black', 10, 4500.00, 4200.00, 1);


-- Sports (Individual Sizes)
INSERT INTO shoes (model_name, brand_id, category_id, size, min_size, max_size, color, stock_quantity, base_price, promotional_price, supplier_id) VALUES
-- Zoom Pegasus 39 - Royal Blue (15 Total -> 4 each approx)
('Zoom Pegasus 39', 1, 8, '8', 8, 8, 'Royal Blue', 4, 55000.00, 52000.00, 2),
('Zoom Pegasus 39', 1, 8, '10', 10, 10, 'Royal Blue', 4, 55000.00, 52000.00, 2),
('Zoom Pegasus 39', 1, 8, '11', 11, 11, 'Royal Blue', 3, 55000.00, 52000.00, 2),

-- Zoom Pegasus 39 - Neon Green (10 Total -> 3, 3, 2, 2)
('Zoom Pegasus 39', 1, 8, '8', 8, 8, 'Neon Green', 3, 54000.00, 51000.00, 2),
('Zoom Pegasus 39', 1, 8, '9', 9, 9, 'Neon Green', 3, 54000.00, 51000.00, 2),
('Zoom Pegasus 39', 1, 8, '10', 10, 10, 'Neon Green', 2, 54000.00, 51000.00, 2);


