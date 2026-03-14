# Event-Driven Microservices with Kafka (KRaft Mode)

This project implements an event-driven microservices architecture using Spring Boot, Apache Kafka (KRaft mode), and Spring Cloud Gateway.

## Architecture Overview

```
Client (Postman)
       ↓
API Gateway (Port 8080)
       ↓
Order Service (Port 8081) → Publishes Event (OrderCreated)
       ↓
Kafka (Port 9092) - KRaft Mode
       ↓
┌──────────────────────────────┐
│                              │
Inventory Service    Billing Service
(Consumes Event)     (Consumes Event)
```

## System Flow

1. Client sends POST request to `/orders` via API Gateway
2. Order Service saves order and publishes `OrderCreated` event to Kafka topic `order-topic`
3. Inventory Service consumes event and updates stock
4. Billing Service consumes event and generates invoice

## Services

| Service | Port | Description |
|---------|------|-------------|
| API Gateway | 8080 | Routes requests to Order Service |
| Order Service | 8081 | REST API for creating orders, publishes events to Kafka |
| Inventory Service | 8082 | Consumes order events and updates stock |
| Billing Service | 8083 | Consumes order events and generates invoices |
| Kafka | 9092 | Message broker (KRaft mode - no ZooKeeper) |

## Prerequisites

- Docker and Docker Compose
- Java 17+
- Maven 3.8+
- Postman (for testing)

## Quick Start with Docker Compose

### Start All Services

```bash
docker-compose up --build
```

This will start:
- Kafka broker (KRaft mode)
- Order Service
- Inventory Service
- Billing Service
- API Gateway

### Stop All Services

```bash
docker-compose down
```

## Running Locally (Without Docker for Services)

### 1. Start Kafka

```bash
docker-compose up kafka
```

### 2. Build and Run Each Service

#### Order Service
```bash
cd order-service
mvn clean package
java -jar target/order-service-1.0.0.jar
```

#### Inventory Service
```bash
cd inventory-service
mvn clean package
java -jar target/inventory-service-1.0.0.jar
```

#### Billing Service
```bash
cd billing-service
mvn clean package
java -jar target/billing-service-1.0.0.jar
```

#### API Gateway
```bash
cd api-gateway
mvn clean package
java -jar target/api-gateway-1.0.0.jar
```

## Testing with Postman

### Create Order

**Endpoint:** `POST http://localhost:8080/orders`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "orderId": "ORD-1001",
  "item": "Laptop",
  "quantity": 1
}
```

**Expected Response:**
```
Order Created & Event Published
```

### Verify Event Processing

Check the logs of Inventory Service and Billing Service to see the event consumption:

```bash
docker-compose logs inventory-service
docker-compose logs billing-service
```

You should see logs like:
```
Inventory Service received OrderCreated event: {...}
Updating stock for order: {...}
Stock updated successfully for order: {...}

Billing Service received OrderCreated event: {...}
Generating invoice for order: {...}
Invoice generated successfully for order: {...}
```

## Kafka Configuration

Kafka is running in KRaft mode (no ZooKeeper required):

- **Broker ID:** 1
- **Node ID:** 1
- **Listeners:** PLAINTEXT://0.0.0.0:9092, CONTROLLER://0.0.0.0:9093
- **Advertised Listeners:** PLAINTEXT://kafka:9092
- **Offsets Topic Replication Factor:** 1

## Project Structure

```
ctse-microservices-lab6/
├── api-gateway/
│   ├── src/main/java/com/example/apigateway/
│   ├── src/main/resources/
│   ├── pom.xml
│   └── Dockerfile
├── order-service/
│   ├── src/main/java/com/example/orderservice/
│   ├── src/main/resources/
│   ├── pom.xml
│   └── Dockerfile
├── inventory-service/
│   ├── src/main/java/com/example/inventoryservice/
│   ├── src/main/resources/
│   ├── pom.xml
│   └── Dockerfile
├── billing-service/
│   ├── src/main/java/com/example/billingservice/
│   ├── src/main/resources/
│   ├── pom.xml
│   └── Dockerfile
├── docker-compose.yml
└── README.md
```

## Technologies Used

- **Spring Boot 3.2.0** - Application framework
- **Spring Cloud Gateway** - API Gateway
- **Spring Kafka** - Kafka integration
- **Apache Kafka 7.6.0** - Message broker (KRaft mode)
- **Docker & Docker Compose** - Containerization
- **Maven** - Build tool

## Reference

This implementation is based on:
https://github.com/seshangamage/Microservices-Architectural-Patterns/tree/main/Kaffka-Example
