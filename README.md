
# Gym CRM – Messaging 

A robust CRM platform tailored for gym and fitness center management. The **Messaging Branch** introduces the messaging component, enabling real-time notifications and communications between services in a microservices-based architecture.

## Table of Contents

* [Overview](#overview)
* [Architecture](#architecture)
* [Features](#features)
* [Getting Started](#getting-started)

  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
  * [Running the Services](#running-the-services)
* [Configuration](#configuration)
* [Usage](#usage)
* [Development & Contribution](#development--contribution)
* [License](#license)
* [Contact](#contact)

## Overview

Gym CRM is a flexible, scalable customer relationship management system built specifically for the gym and fitness industry. It manages memberships, training schedules, and customer data. The **Messaging Branch** introduces messaging capabilities to the system, enabling event-driven communication between the core CRM components (e.g., sending notifications, managing real-time alerts, and enabling asynchronous messaging via a message broker like Kafka or RabbitMQ).

This messaging layer integrates seamlessly with the microservices architecture, decoupling communication and providing a flexible, scalable solution for future expansion.

## Architecture

The Gym CRM system is built on a **microservices architecture**. Each service is designed to operate independently while communicating with other services via an event-driven messaging layer. Services register with **Eureka** for service discovery, enabling automatic scaling and communication.

Key components:

* **Eureka Server**: Centralized service discovery for all microservices.
* **Core CRM Service**: Manages member data, workout plans, and gym activities.
* **Workload Service**: Handles trainer schedules, bookings, and gym resources.
* **Messaging Service**: Manages real-time notifications, push notifications, and system messaging.

Each service interacts with others using **Kafka** or **RabbitMQ** for message passing, allowing for asynchronous and decoupled interactions.

## Features

* **Modular Microservices**: Independent, scalable services for different aspects of gym management.
* **Real-Time Messaging**: Allows services to send and receive asynchronous messages, enabling real-time updates and notifications.
* **Service Discovery**: Dynamic service registration and discovery using **Eureka**.
* **Push Notifications**: Notifies gym members and staff about updates, reminders, and alerts.
* **Highly Configurable**: Easily configurable for different gym types and environments.
* **Scalability**: Horizontally scalable to handle increased load and demands.

## Getting Started

### Prerequisites

Before getting started, ensure the following are installed and configured:

* **Java 17** (or compatible version)
* **Maven 3.x**
* **Message Broker** (Kafka or RabbitMQ)
* **Database** (e.g., PostgreSQL, MySQL)
* Optional: **Docker** and **Docker Compose** for containerized setup.

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/lasha-gzobava/gym_crm.git
   cd gym_crm
   git checkout messaging
   ```

2. Install dependencies:

   ```bash
   mvn clean install
   ```

### Running the Services

Once the project is built, you can run the services in the following order:

1. **Start Eureka Server**:
   The Eureka server is the registry for microservices.

   ```bash
   cd eureka_server
   mvn spring-boot:run
   ```

2. **Start Core CRM Service**:
   This service manages members, workouts, and sessions.

   ```bash
   cd ../main_service
   mvn spring-boot:run
   ```

3. **Start Workload Service**:
   Manages gym resources like trainers and booking schedules.

   ```bash
   cd ../workload_service
   mvn spring-boot:run
   ```

4. **Start Messaging Service**:
   Manages notifications, events, and messaging.

   ```bash
   cd ../messaging_service
   mvn spring-boot:run
   ```

Each service will register with the Eureka server and communicate with others using the message broker.

## Configuration

Configuration is managed through `application.yml` (or `application-<profile>.yml` for environment-specific configurations). Key configuration areas include:

* **Service Discovery**:

  ```yaml
  spring:
    cloud:
      netflix:
        eureka:
          client:
            service-url: http://localhost:8761/eureka
  ```

* **Database Configuration**:

  ```yaml
  spring:
    datasource:
      url: jdbc:mysql://localhost:3306/gymcrm
      username: root
      password: password
  ```

* **Messaging (Kafka/RabbitMQ)**:

  ```yaml
  spring:
    kafka:
      bootstrap-servers: localhost:9092
  ```

* **Ports & Health Check**:

  ```yaml
  server:
    port: 8080
  management:
    endpoints:
      web:
        exposure:
          include: "*"
  ```

Make sure to update these properties according to your environment.

## Usage

Once all services are running:

* **Core CRM Service**: Manages members, sessions, and billing. Access through REST APIs or the front-end UI.
* **Messaging Service**: Handles notifications such as reminders, booking confirmations, and alerts. Integrate with your application or use the message bus to listen for incoming events.
* **Workload Service**: Allows staff to manage trainer schedules and available resources, helping to keep the gym running smoothly.

Example API calls:

* **Send Notification**:
  Use the messaging service to send a notification to a user about an upcoming class.

  ```bash
  POST /api/notifications/send
  Content-Type: application/json
  {
    "userId": 123,
    "message": "Your workout session starts in 15 minutes!"
  }
  ```

## Development & Contribution

We welcome contributions from the community. To contribute:

1. Fork the repository and create your branch.
2. Write unit tests and integration tests for new features.
3. Follow the existing code style, naming conventions, and API documentation practices.
4. Submit a pull request with a detailed description of your changes.





## Contact

**Author**: Lasha Gzobava
**GitHub Repository**: [https://github.com/lasha-gzobava/gym_crm](https://github.com/lasha-gzobava/gym_crm)
For support or issues, please open an issue or contact us via GitHub.


