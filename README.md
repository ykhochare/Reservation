# 🏖️ Silver Heavens Resort — Reservation System

A full-featured resort reservation management system built with **Java 17**, **Spring Boot 3.2.5**, and **MySQL**.

---

## 🛠️ Tech Stack

| Technology | Details |
|------------|---------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Database | MySQL |
| Build Tool | Gradle |
| ORM | Spring Data JPA (Hibernate) |
| Messaging | RabbitMQ |
| Batch | Spring Batch |
| Documentation | SpringDoc OpenAPI (Swagger UI) |
| Excel | Apache POI |

---

## 🚀 Getting Started

### Prerequisites
- Java 17
- MySQL
- RabbitMQ

### Configuration
Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/silver_heavens
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email
spring.mail.password=your_password
```

### Run
```bash
./gradlew bootRun
```

App runs on: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

## 📦 Modules

### 🏠 Bungalow
```
POST   /api/bungalows                      → Add bungalow
PUT    /api/bungalows/{bungalowId}         → Update bungalow
GET    /api/bungalows/{bungalowId}         → Get by ID
GET    /api/bungalows                      → Get all
GET    /api/bungalows/{bungalowId}/revenue → Get revenue
```

### 📅 Bungalow Availability
```
POST   /api/bungalows/{bungalowId}/availability                 → Add availability
GET    /api/bungalows/{bungalowId}/availability                 → Get all availability
GET    /api/bungalows/{bungalowId}/availability/check?from=&to= → Check availability
PATCH  /api/bungalows/{bungalowId}/availability/{id}            → Update status
DELETE /api/bungalows/{bungalowId}/availability/{id}            → Delete record
```

### 📋 Reservation
```
POST   /api/reservations/{bungalowId}               → Create reservation
PATCH  /api/reservations/confirm/{id}               → Confirm reservation
PATCH  /api/reservations/{id}/checkout              → Checkout
PATCH  /api/reservations/confirm-by-agent/{agentId} → Bulk confirm by agent
GET    /api/reservations/{id}                       → Get by ID
GET    /api/reservations                            → Get all (filters: status, dates, bungalowId)
```

### ❌ Cancellation
```
POST   /api/cancellations/cancel/{reservationId}      → Cancel reservation
GET    /api/cancellations                              → Get all (filters: status, policyId)
GET    /api/cancellations/{cancellationId}             → Get by ID
GET    /api/cancellations/reservation/{reservationId}  → Get by reservation
```

### 📜 Cancellation Policy
```
POST   /api/cancellation-policies      → Create policy
PUT    /api/cancellation-policies/{id} → Update policy
DELETE /api/cancellation-policies/{id} → Delete policy
GET    /api/cancellation-policies/{id} → Get by ID
GET    /api/cancellation-policies      → Get all
```

### 💳 Payment
```
POST   /api/payments/pay/{reservationId} → Make payment
GET    /api/payments/{reservationId}     → Get all payments for reservation
```

### 👤 Guest
```
POST   /api/guests/register   → Register guest
GET    /api/guests/{guestId}  → Get by ID
PUT    /api/guests/{guestId}  → Update guest
GET    /api/guests/email      → Get by email
```

### 🤝 Travel Agent
```
POST   /api/agents            → Register agent
GET    /api/agents/{agentId}  → Get by ID
PUT    /api/agents/{agentId}  → Update agent
GET    /api/agents            → Get all
```

### 💰 Agent Commission
```
GET    /api/commissions/agent/{agentId}                                → Get all commissions
GET    /api/commissions/agent/{agentId}/commission-statement?yearMonth= → Monthly statement
GET    /api/commissions/recovery-required                               → Commissions needing recovery
PATCH  /api/commissions/{commissionId}/pay                             → Mark as paid
```

### 🌟 Loyalty Points
```
GET    /api/pointsHistory/guest/{guestId} → Get points history
```

### 📊 Excel
```
GET    /api/excel/reservation/download → Export reservations to Excel
POST   /api/excel/reservation/upload  → Import reservations from Excel
```

---

## ⚙️ Batch Jobs

| Job | Description | Schedule |
|-----|-------------|----------|
| ReservationExpiryBatch | Expires PENDING reservations older than 24 hours | Every night 12:00 AM |
| RefundReconciliationBatch | Marks refunds OVERDUE if PENDING for 48+ hours | Every morning 8:00 AM |
| ExpireWaitingReservationBatch | Expires WAITING reservations where arrival date passed | Every night 12:00 AM |
| LoyaltyPointsExpiryBatch | Expires loyalty points older than 1 year | Every night 12:00 AM |

---

## 📨 Event Driven Architecture

### Spring Events + @Async (Cancellation Flow)
On reservation cancellation, `ReservationCancelledEvent` is published and handled asynchronously:
- Commission reversal
- Loyalty points deduction
- Waiting list promotion
- Email notification

### RabbitMQ (Email)
- Emails sent via `email.queue`
- Used for reservation confirmation and payment success notifications

---

## 🗂️ Entity Overview

```
Bungalow ──────────────── BungalowAvailability
    │
    ├──────────────────── Reservation ─────── Payment
                              │
                              ├────────────── Guest ─── LoyaltyPointsHistory
                              ├────────────── TravelAgent
                              ├────────────── AgentCommission
                              └────────────── Cancellation ─── CancellationPolicy
```

---

## 📌 Reservation Status Flow

```
PENDING → CONFIRMED → COMPLETED
    │          │
    ↓          ↓
EXPIRED    CANCELLED

WAITING → CONFIRMED (auto-promoted on cancellation)
```
