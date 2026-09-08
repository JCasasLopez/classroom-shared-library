# Shared Library

Centralized Java library for the Classrooms microservice architecture (see full [technical documentation](#) for details).

The entire Classrooms application is deployed and available at [www.book-your-classroom.com](https://www.book-your-classroom.com).

## Table of Contents
- [Purpose](#purpose)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Dependency Setup](#dependency-setup)
- [Tests](#tests)
- [Contribution and License](#contribution-and-license)
- [Contact](#contact)

## Purpose

The purpose of this repository is to provide a centralized Java library for the Classrooms microservice architecture. It offers unified data structures, security mechanisms (such as base JWT filters and context propagation), and shared Kafka event models to enforce architectural consistency and eliminate code duplication across services.

## Features

* **Common Data Structures:** Unified HTTP wrappers (`StandardResponse`), domain enumerations, and custom exception hierarchy for consistent error handling.
* **Kafka Event Payloads:** Shared event definitions (`ClassroomEvent`, `NotificationEvent`) to ensure contract consistency across message-driven workflows.
* **Security Layer:** JWT token validation (`JwtService`), configurable token builder (`GenerateJwt`), and an abstract base filter (`AuthenticationFilterBase`) implementing the Template Method pattern.
* **Context Propagation:** ThreadLocal-based context manager (`UserContext`) to seamlessly carry user identity (`UserInfo`) across request execution threads.

## Tech Stack

- Java 17
- JJWT Library
- Maven

**Testing**
- JUnit 5
- Mockito

## Dependency Setup

To import this library into a consuming microservice, add the JitPack repository and the dependency to `pom.xml`:

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.jcasaslopez</groupId>
    <artifactId>classroom-shared-library</artifactId>
    <version>0.0.5</version>
</dependency>
```

## Tests

The library includes a focused unit test suite validating custom security components and domain logic without framework overhead:

* **`AuthBaseFilterUnitTest`:** Verifies base authentication filter behavior, HTTP error responses (401 and 403), `UserContext` lifecycle management, and mandatory memory cleanup upon request completion.
* **`JwtServiceUnitTest`:** Evaluates token validation against various edge cases via parameterized tests (missing Bearer prefix, expired claims, malformed signatures, token type mismatches) and role-based authorization rules.

Run tests locally via Maven:
```bash
mvn clean test
```

## Contribution and License
### Contributing
As this project is intended as a personal demo, external contributions are not being accepted at this time.

### License
This project is licensed under the MIT License.  
See the [LICENSE](./LICENSE) file for details.

## Contact
Created by Jorge Casas López.  
Email: [j.casas.lopez.26@gmail.com](mailto:j.casas.lopez.26@gmail.com) 
