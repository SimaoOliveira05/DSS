# bUMger Kings

> Integrated management system for a fast-food chain, handling everything from customer orders to kitchen production and delivery.

## Final Grade
**XX** / 20

## Authors
* **[Student Name]**
* **[Student Name]**
* **[Student Name]**

## About the Project
This project consists of a management system for a restaurant chain (bUMger Kings), developed to automate restaurant operations and provide management indicators.

**Project Focus:**
The primary goal of this course was to prioritize **software modeling** and design patterns before implementation. Extensive design work was conducted to ensure a robust architecture, producing artifacts such as:
* Domain Models
* Class Diagrams
* Package Diagrams
* Sequence Diagrams

The system was implemented using a strict layered architecture (Business Logic, Data, Interface) to support the full lifecycle of an order and operational management.

### Key Features
* **Order Management:** Custom order creation, payment processing, and status tracking (Preparing, Ready, Delivered).
* **Production System:** Automatic decomposition of orders into specific tasks for kitchen stations (Grill, Fryer, Assembly).
* **Management & Admin:** Performance monitoring (KPIs), stock management, and staff messaging.

## Technologies
* **Language:** Java (JDK 21)
* **Build Tool:** Maven
* **Database:** MySQL (via JDBC)
* **Modeling:** Visual Paradigm
* **Concepts:** Layered Architecture, DAO Pattern, Singleton, Facade

## How to Run
Ensure you have Java and Maven installed.

```bash
# Compile the project
mvn clean install

# Run the application
java -cp target/bUMguerKings-1.0-SNAPSHOT-jar-with-dependencies.jar org.App
