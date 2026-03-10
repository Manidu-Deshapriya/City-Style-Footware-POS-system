# City Style Footwear - Advanced POS System

City Style POS is a robust desktop application built using **Java Swing** and **PostgreSQL**. It is designed to handle the core operations of a footwear retail store, including inventory management, real-time sales processing, and secure manager-authorized discount workflows.

---

## 🚀 Key Features

*   **Role-Based Access Control:** Secure login gateways for Managers and Cashiers with distinct dashboard interfaces.
*   **Inventory Management:** Full CRUD (Create, Read, Update, Delete) operations for tracking shoes, categories, brands, and suppliers.
*   **Point of Sale (POS) Terminal:** Efficient cart management, real-time bill calculation, and secure transaction logging.
*   **Managerial Discount Approval:** A digital request-approval system to ensure all high-value discounts are authorized by a manager.
*   **Return Management:** Process product returns with automatic restocking and audit logging.
*   **Dynamic UI Categorization:** Hash-based color coding for instant visual identification of stock by category or model.
*   **Input Validation:** Robust document filters to prevent invalid data entry and maintain system integrity.

---

## 🛠 Technology Stack

*   **Language:** Java JDK 17
*   **Front-end:** Java Swing & AWT (Modern Flat Theme)
*   **Database:** PostgreSQL 16
*   **Connectivity:** JDBC (Java Database Connectivity)
*   **Architecture:** MVC (Model-View-Controller) with DAO Pattern

---

## ⚙️ Installation and Setup Guide

Follow these steps to set up the system on your local machine:

### 1. Prerequisites
Ensure you have the following installed:
*   **JDK 17** or higher.
*   **PostgreSQL 16** server.
*   An IDE (VS Code, IntelliJ, or NetBeans).
*   **PostgreSQL JDBC Driver** (.jar file).

### 2. Database Configuration
1.  Open **pgAdmin 4** or any PostgreSQL client.
2.  Create a new database named: `city_style_pos`.
3.  Open the **Query Tool** for the created database.
4.  Open the `database/setup.sql` file from this project.
5.  Copy the SQL script and **Execute** it to create tables and insert default data.

### 3. Application Configuration
1.  Navigate to your database connection utility class (e.g., `DatabaseConnection.java`).
2.  Update the database URL, username, and password fields:
    ```java
    private static final String URL = "jdbc:postgresql://localhost:5432/city_style_pos";
    private static final String USER = "your_postgres_username";
    private static final String PASS = "your_postgres_password";
    ```

### 4. Build and Run
1.  Import the project into your IDE.
2.  Add the **PostgreSQL JDBC Connector** to your project's library/build path.
3.  Run the [Main.java](cci:7://file:///e:/Projects/Java/city%20pos/src/com/citystyle/Main.java:0:0-0:0) file to launch the application.

---

## 🔑 Default Login Credentials

| Role | Username | Password |
| :--- | :--- | :--- |
| **Manager** | `admin` | `admin123` |
| **Cashier** | `cashier` | `cashier123` |

---

## 📂 Project Structure

*   `src/com/citystyle/model`: Data entities (User, Shoe, Sale, etc.)
*   `src/com/citystyle/dao`: Database interaction logic.
*   `src/com/citystyle/ui`: User interface components and dashboards.
*   `database/`: Contains `setup.sql` for environment initialization.

---

## Evaluation and Impact
The system demonstrates high technical stability and exceptional adherence to **OOP Principles**. It provides a scalable architecture suitable for retail expansion, ensuring both data integrity and a premium user experience.
