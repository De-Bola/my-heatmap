# 📈 Heatmap API — Spring Boot Backend

This is a backend service that generates **call heatmaps** based on call logs, showing hourly **answer rates**.

Built with **Spring Boot 3.4**, **JPA**, **Flyway**, **H2 database**, and **Spring Security**.

---

## 🚀 Features

- 📊 **Hourly Answer Rate** API — `/api/heatmap/answer-rate`
- 🔒 Basic authentication (default: `admin/admin123`)
- 📈 Database migrations managed via Flyway
- 🧪 Full integration tests for controller and data access
- ⚡ Built with Lombok, Flyway, Spring Data JPA

---

## 📚 Tech Stack

| Technology         | Purpose                           |
|:--------------------|:----------------------------------|
| Spring Boot         | Core backend framework            |
| Spring Security     | API authentication                |
| Spring Data JPA     | ORM for database access           |
| H2 Database         | In-memory database (for dev/test) |
| Flyway              | DB migrations                     |
| Lombok              | Boilerplate code reduction        |
| Gradle (Kotlin DSL) | Build tool                        |
| JUnit + Spring Test | Integration testing               |

---

## 🛠️ Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/De-Bola/my-heatmap.git
cd my-heatmap
```
---
## 2. Run the application

Use Gradle to start the app:

```bash
./gradlew bootRun
```

Once up and running, the API will be accessible at:
- **Backend API**: [http://localhost:8080/api](http://localhost:8080/api)
- 
---

## ⚙️ Technology Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- Spring Security
- H2 In-Memory Database
- Flyway (for DB migrations)
- Lombok (boilerplate reduction)

## 🧪 Running Tests

Run all tests using:

```bash
./gradlew test
```

### 🧪 Test Coverage

Test coverage includes:

- ✅ Integration tests for the API endpoints
- ✅ Repository layer validations
- ✅ Database seed and migration verification

Main integration tests are located in:

- [`HeatMapControllerIntegrationTest.java`](src/test/java/com/onoff/heatmap/HeatMapControllerIntegrationTest.java)
---

## 📋 API Overview
To see the API doc use the following [link](http://localhost:8080/swagger-ui/index.html) when the app is running.

| Method | Endpoint | Purpose |
|:------|:---------|:--------|
| `GET` | `/api/heatmap/answer-rate` | Fetch hourly answer rates for a given date |

With basic auth credentials - admin : admiin123

Example usage:

```bash
curl "http://localhost:8080/api/heatmap/answer-rate?date=2025-04-20"
```

---

## 🚀 Future Improvements

- Support external production databases (PostgreSQL, MySQL)
- Set up CI/CD pipelines
- Expand analytics (e.g., busiest call time prediction)
