# Fraud Rule Engine Service

A production-grade Spring Boot application that processes financial transactions and flags potential fraud using configurable rules.

## Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Project Structure](#project-structure)

## Overview

The Fraud Rule Engine Service evaluates incoming transactions against a set of configurable fraud detection rules. Each rule analyzes specific transaction attributes and assigns a risk score when suspicious patterns are detected.

### Key Features
- **Extensible Rule Engine** - Strategy pattern for easy addition of new fraud rules
- **Real-time Processing** - Immediate evaluation of transactions
- **Risk Scoring** - Each rule generates a risk score (0-100)
- **Alert Management** - Flagged transactions create reviewable alerts
- **RESTful API** - Complete API for transaction processing and alert retrieval

### Implemented Fraud Rules
| Rule | Description | Risk Score |
|------|-------------|------------|
| **High Amount Rule** | Flags transactions exceeding R50,000 | 70-100 |
| **Suspicious Merchant Rule** | Flags high-risk merchant categories (gambling, crypto, etc.) | 60-80 |
| **Cross Border Rule** | Flags foreign currency transactions | 60-85 |

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        REST API Layer                           │
│              TransactionController │ FraudAlertController       │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Service Layer                                │
│                   FraudDetectionService                          │
│         (Orchestrates rule evaluation and alert creation)        │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                 Rule Engine (Strategy Pattern)                   │
│     HighAmountRule │ SuspiciousMerchantRule │ CrossBorderRule   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Repository Layer                              │
│          TransactionRepository │ FraudAlertRepository            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      PostgreSQL Database                         │
└─────────────────────────────────────────────────────────────────┘
```

## Technologies

- **Java 17**
- **Spring Boot 3.2.3**
- **Spring Data JPA**
- **PostgreSQL** (Production)
- **H2 Database** (Testing)
- **Maven**
- **Docker & Docker Compose**
- **JUnit 5 & Mockito**

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- Docker & Docker Compose (for containerized deployment)

### Clone the Repository
```bash
git clone https://github.com/tapsshore/fraud-rule-engine.git
cd fraud-rule-engine
```

## Running the Application

### Option 1: Using Docker (Recommended)

```bash
# Build and run with Docker Compose
docker-compose up --build

# Run in detached mode
docker-compose up -d --build

# Stop the application
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

The application will be available at `http://localhost:8080`

### Option 2: Local Development

```bash
# Build the project
mvn clean install

# Run with Maven
mvn spring-boot:run

# Or run the JAR directly
java -jar target/fraud-rule-engine-0.0.1-SNAPSHOT.jar
```

**Note:** For local development, you'll need a PostgreSQL instance or configure H2 for development.

## API Documentation

### Base URL
```
http://localhost:8080/api/v1
```

### Endpoints

#### Process Transaction
```http
POST /api/v1/transactions
Content-Type: application/json

{
  "transactionId": "TXN-001",
  "accountId": "ACC-12345",
  "amount": 75000.00,
  "currency": "ZAR",
  "transactionType": "TRANSFER",
  "merchantName": "Test Merchant",
  "merchantCategory": "RETAIL",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Response:**
```json
{
  "transaction": {
    "id": 1,
    "transactionId": "TXN-001",
    "accountId": "ACC-12345",
    "amount": 75000.00,
    "currency": "ZAR",
    "status": "FLAGGED"
  },
  "alerts": [
    {
      "id": 1,
      "ruleName": "HIGH_AMOUNT_RULE",
      "ruleDescription": "Transaction amount 75000.00 exceeds threshold of 50000",
      "riskScore": 75,
      "alertStatus": "PENDING"
    }
  ],
  "flagged": true,
  "totalRiskScore": 75
}
```

#### Get All Transactions
```http
GET /api/v1/transactions
```

#### Get Transaction by ID
```http
GET /api/v1/transactions/{transactionId}
```

#### Get Transactions by Account
```http
GET /api/v1/transactions/account/{accountId}
```

#### Get All Alerts
```http
GET /api/v1/alerts
```

#### Get Alert by ID
```http
GET /api/v1/alerts/{id}
```

#### Get Alerts by Status
```http
GET /api/v1/alerts/status/{status}
```
Status values: `PENDING`, `REVIEWING`, `CONFIRMED`, `DISMISSED`

#### Get High Risk Alerts
```http
GET /api/v1/alerts/high-risk
```

#### Get Alerts by Rule
```http
GET /api/v1/alerts/rule/{ruleName}
```

### Health Check
```http
GET /actuator/health
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=FraudDetectionServiceTest
```

### Run Tests with Coverage
```bash
mvn test jacoco:report
```

### Test Categories
| Category | Description | Count |
|----------|-------------|-------|
| Unit Tests | Domain entities, rules | 13 |
| Service Tests | FraudDetectionService with mocks | 6 |
| Repository Tests | JPA repositories with H2 | 10 |
| Controller Tests | WebMvcTest for REST endpoints | 10 |
| Integration Tests | Full context load | 1 |

**Total: 40 tests**

## Project Structure

```
fraud-rule-engine/
├── src/
│   ├── main/
│   │   ├── java/za/co/capitecbank/fraudruleengine/
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── domain/           # JPA entities
│   │   │   ├── dto/              # Data transfer objects
│   │   │   ├── exception/        # Custom exceptions & handlers
│   │   │   ├── repository/       # Spring Data repositories
│   │   │   ├── rule/             # Fraud detection rules
│   │   │   ├── service/          # Business logic
│   │   │   └── FraudRuleEngineApplication.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── application-docker.yaml
│   └── test/
│       ├── java/                 # Test classes
│       └── resources/
│           └── application-test.yaml
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Configuration

### Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | Database URL | - |
| `SPRING_DATASOURCE_USERNAME` | Database username | - |
| `SPRING_DATASOURCE_PASSWORD` | Database password | - |
| `FRAUD_RULES_HIGH_AMOUNT_THRESHOLD` | High amount threshold | 50000 |
| `FRAUD_RULES_CROSS_BORDER_LOCAL_CURRENCIES` | Local currencies | ZAR |

## Design Patterns Used

1. **Strategy Pattern** - Fraud rules implement a common interface, allowing easy addition of new rules
2. **Builder Pattern** - Used for constructing complex objects (entities, DTOs)
3. **Repository Pattern** - Data access abstraction via Spring Data JPA
4. **DTO Pattern** - Separation between API contracts and domain entities

## License

This project is created for evaluation purposes.

---

**Author:** Tapiwanashe Shoshore
**Date:** 06 March 2026
