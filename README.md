



# Gym Training Management System

A **Spring**-based REST API for managing trainees, trainers, trainings, and user accounts. The system handles authentication, active status toggling, profile updates, and training schedules — all built on a secure and modular layered architecture.

---

##  Features

* **Trainee Management**
  Register, view, update, delete, activate/deactivate, assign trainers

* **Trainer Management**
  Register, view, update, activate/deactivate, list unassigned trainers

* **Training Management**
  View trainings by filters (period, type, trainer, trainee)

* **User Authentication**
  Password-secured access, active user check, bcrypt encryption

* **Logging**
  Transactional logging with trace IDs (`UUID`) for traceability

* **Swagger/OpenAPI**
  Documented via annotations (`@Operation`, `@Tag`) for easy testing

---

##  Architecture

* **Spring Boot**
* **Layered Architecture**

  * `Controller` – REST endpoints
  * `Service` – Business logic
  * `Repository` – JPA + Spring Data
  * `DTO` – Decoupled API contracts
* **Auth** – Manual validation using `UserService`
* **Logging** – Via `Slf4j` in all layers
* **Validation** – JSON structure + basic null/empty checks

---

##  Project Structure

```
src/main/java/org/example/
├── controller/          # REST controllers
├── dto/                 # Data Transfer Objects
├── entity/              # JPA Entities
├── repository/          # Spring Data Repos
├── service/             # Service interfaces & impl
├── util/                # Utilities (e.g., password generator)
└── GymTrainingApp.java  # Spring Boot entrypoint
```

---

##  Testing

* **JUnit 5** + **Mockito**
* Test coverage includes:

  * Service layer logic
  * Authentication logic
  * Integration-style controller tests

Run tests:

```bash
mvn test
```

---

##  API Usage

 Use Postman or Swagger UI to test:

### Authentication Format

> All endpoints requiring auth must include `username` and `password` as query parameters.

```http
GET /trainee/profile?username=john.doe&password=password
```

### JSON Example – Register Trainee

```json
POST /trainee/register
{
  "user": {
    "firstName": "John",
    "lastName": "Doe"
  },
  "dateOfBirth": "2000-01-01",
  "address": "123 Main St"
}
```

---

## Summary

*  Clean modular Spring Boot app
*  Secure auth with bcrypt and manual control
*  Fully testable service structure
*  DTOs ensure loose coupling
*  Logging everywhere for debug and trace

---


