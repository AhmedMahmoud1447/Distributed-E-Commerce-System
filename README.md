# Distributed E-Commerce Microservices Platform
---
A distributed E-Commerce microservices platform built with Spring Boot 3, Apache Kafka (Event-Driven Saga), and Redis Caching. Features Database-per-Service (PostgreSQL), Netflix Eureka, and API Gateway routing—fully containerized with Docker.

## 🏗️ System Architecture

<img width="1536" height="1024" alt="sys5" src="https://github.com/user-attachments/assets/d3b82a70-730f-40d7-916c-f70f3d5cf2fa" />

### Microservices E-Commerce System Architecture
A complete **Microservices-based E-Commerce Architecture** built using:

- Spring Boot
- Spring Cloud Gateway
- Netflix Eureka Service Discovery
- Apache Kafka
- PostgreSQL
- Redis Cache
- REST / Feign Client Communication
- Event-Driven Saga Pattern 
- Database-per-Service Pattern

---

### Architectural Components:
* **API Gateway (Port 8080):** The single entry point for all clients, handling dynamic routing, load balancing, and request filtering powered by Spring Cloud Gateway's high-performance, non-blocking routing engine.
* **Service Discovery (Port 8761):** Powered by Netflix Eureka, enabling dynamic service registration, health heartbeats, and client-side discovery.
* **Order Service (Port 8081):** Manages order lifecycles, persists data to order_db, and manages distributed Sagas.
* **Product Service (Port 8082):** Handles product catalog management with high-speed Redis Caching layer over product_db for optimized read performance.
* **Inventory Service (Port 8083):** Manages stock allocations and strict validations within inventory_db.
* **Payment Service (Port 8084):** Simulates asynchronous payment authorization and transactions within payment_db.
* **Notification Service (Port 8085):** A standalone microservice that asynchronously consumes event streams to trigger real-time client notifications via email.
---

## 🛠️ Tech Stack & Architectural Patterns

* **Core Framework:** Spring Boot 3 & Spring Cloud (Gateway, Eureka)
* **Database-per-Service:** Isolated PostgreSQL 15 instances for independent data domains, eliminating tight data coupling.
* **Asynchronous Messaging:** Apache Kafka acts as the highly-scalable distributed Event Bus.
* **Distributed Transactions:** Saga Pattern with comprehensive compensating transactions (Rollbacks).
* **Caching Strategy:** Redis cache-aside pattern for catalog optimization.
* **DevOps & Containerization:** Fully containerized setup via multi-stage Docker builds and Docker Compose orchestration.

---

## 🔄 Core Workflow (The Saga Pattern)

### 🟢 Success Path Flow
1. **Client** hits the `API Gateway` to place an order.
2. `Order Service` verifies stock synchronously via **Feign Client** from `Inventory Service`.
3. If stock is available, `Order Service` creates a pending order and publishes an `OrderCreatedEvent` to Kafka.
4. `Payment Service` consumes the event, successfully processes the transaction, and emits a `PaymentSuccessEvent`.
5. `Order Service` reacts to the success event and updates the order status to **CONFIRMED**.
6. `Notification Service` captures the success event and delivers a confirmation email to the user.

### 🔴 Rollback / Compensating Flow (On Payment Failure)
1. If the credit check or transaction fails, `Payment Service` publishes a `PaymentFailedEvent`.
2. `Order Service` consumes the failure event and executes a compensating transaction to mark the order status as **CANCELLED**.
3. `Inventory Service` consumes the failure event to roll back stock reservation (**Restock/Release** inventory).
4. `Notification Service` alerts the user via email regarding the order cancellation.

---

## 🚀 How to Run the System Locally

The entire system infrastructure and applications are fully dockerized. You do not need to install Java, PostgreSQL, or Kafka on your host machine.

### Prerequisites
* Ensure **Docker Desktop** is installed and running.

### Setup Steps
1. Clone this repository:
   ```bash
   git clone [https://github.com/AhmedMahmoud1447/ecommerce-microservices.git](https://github.com/AhmedMahmoud1447/ecommerce-microservices.git)
   cd ecommerce-microservices
    ```
2. Spin up the entire infrastructure cluster and applications with a single command:
 ```bash
docker compose up --build -d
 ```

