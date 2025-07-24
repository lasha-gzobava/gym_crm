#  Gym CRM System

A Spring Core-based CRM system for managing trainees, trainers, and training sessions using layered architecture and in-memory storage.

---

##  Features

- **Trainee Management**: CRUD operations
- **Trainer Management**: CRU operations
- **Training Management**: CR operations
- **User Management**: Unique usernames, secure passwords, auto-incremented IDs
- **JSON Seeding**: Loads sample data at startup

---

##  Architecture

- **Spring Core** (no Spring Boot)
- **Layered Design**:
  - DAO Layer (`Map`-based storage)
  - Service Layer (business logic)
  - Facade Layer (`GymFacade` as unified API)
- **Vertical Partitioning**: Shared `User` object inside `Trainee` and `Trainer`
- **Component Scanning** via `@ComponentScan`

---

##  Structure

```

src/main/java/org/example/
├── config/
├── dao/
├── entity/
├── service/
├── facade/
├── seed/
└── Main.java

````

---

##  Testing

- Written with **JUnit 5** + **Mockito**
- Tests for:
  - DAO & Services
  - Facade (integration)
  - Seeding logic

Run tests:

```bash
mvn clean test
````



---

##  Summary

* Spring Core, modular & testable
* Clean separation of concerns
* No external DB – lightweight & portable



