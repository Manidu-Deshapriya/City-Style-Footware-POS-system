Installation and Setup Guide
Follow these steps to set up the project on your local machine:

1. Prerequisites
Ensure you have the following installed:

Java Development Kit (JDK) 17 or higher.
PostgreSQL 16 (or comparable version).
A Java IDE (Visual Studio Code, IntelliJ IDEA, or NetBeans).
2. Database Environment Setup
Open your PostgreSQL management tool (e.g., pgAdmin 4).
Create a new database named city_style_pos.
Open the Query Tool for the city_style_pos database.
Locate the setup.sql file (found in the root/database directory of this project).
Copy the contents of setup.sql and execute it in the Query Tool.
This will create all necessary tables (users, shoes, sales, returns, discount_requests) and insert default data.
3. Database Connection Configuration
Open the source code and navigate to the database connection utility class (e.g., DatabaseConnection.java).
Update the following fields to match your local PostgreSQL credentials:
URL: jdbc:postgresql://localhost:5432/city_style_pos
USER: Your PostgreSQL username (default is postgres).
PASSWORD: Your PostgreSQL password.
4. Running the Application
Import the project into your preferred IDE.
Ensure the PostgreSQL JDBC driver (.jar file) is added to your project's Build Path/Libraries.
Locate the 

Main.java
 file (or the entry point class).
Run the class as a Java Application.
Log in using the default credentials (provided in the setup.sql or 

UserDAO
 documentation).
Default Login Credentials
Manager: username: admin / password: 123
Cashier: username: cashier / password: 123
Project Structure
src/com/citystyle/model: POJO classes representing database entities.
src/com/citystyle/dao: Data Access Objects for SQL interaction.
src/com/citystyle/ui: Java Swing layouts and dashboard logic.
database/setup.sql: Database schema initialization script.
