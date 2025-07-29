
# Gym CRM System (Hibernate Edition)

A Spring Core + Hibernate-based CRM system for managing trainees, trainers, and training sessions with full persistence using PostgreSQL and layered architecture.

---

## Features

* **Trainee Management**: CRUD operations
* **Trainer Management**: CRU operations
* **Training Sessions**: Creation and listing
* **Training Types**: Seeded and assignable
* **User Handling**: Unique usernames, active status toggle, and secure hashed passwords
* **Database Seeding**: Automatic JSON-based data import on startup

---

## Architecture

* **Spring Core** (no Spring Boot)
* **JPA with Hibernate**
* **Layered Design**:

  * **Repository Layer** (`JpaRepository` interfaces)
  * **Service Layer** (business logic)
  * **Facade Layer** (`GymFacade` as unified API)
* **Entities**: Fully normalized, with relationships (e.g. `@ManyToOne`, `@OneToOne`)
* **Transactional Management**: Spring’s `@Transactional` for consistency
* **Seed File Integration**: Uses `@PostConstruct` to load demo data

---

## Structure

```
src/main/java/org/example/
├── config/
├── dto/
├── entity/
├── repository/
├── service/
├── facade/
├── seed/
└── Demo.java
```

---

## Testing

* Built with **JUnit 5** and **Mockito**
* Coverage includes:

  * Service & Repository logic
  * Facade layer (integration)
  * Seed loader verification

Run tests:

```bash
mvn clean test
```

---

## Setup

* **PostgreSQL** required (default DB: `gym_crm`)
* Configure DB connection in `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gym_crm
spring.datasource.username=postgres
spring.datasource.password=your_password
```

---

## Summary

* Modular, layered architecture using **Hibernate ORM**
* Clean integration with PostgreSQL
* Easy-to-extend, well-tested structure
* Includes real-world concerns like cascade deletes, validation, and seeding

---

