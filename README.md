# School Management System API

A production-ready RESTful backend application for managing school operations, built using Java 21, Spring Boot 3, Spring Security, JWT Authentication, OAuth2 Login, MySQL, Docker, and JUnit.

This project demonstrates modern backend development practices including secure authentication, role-based authorization, layered architecture, validation, exception handling, and automated testing.

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-green)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6-orange)
![JWT](https://img.shields.io/badge/Auth-JWT-yellow)
![OAuth2](https://img.shields.io/badge/OAuth2-Google%20%7C%20GitHub-red)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue)
![Docker](https://img.shields.io/badge/Container-Docker-blue)

---

## Overview

The School Management System API is designed to simplify school administration by providing secure and scalable APIs for managing users, students, teachers, parents, classes, and fee records.

The application follows a layered architecture and incorporates industry-standard security practices using JWT-based authentication and OAuth2 social login.

---

## Features

### Authentication & Authorization

* JWT-based stateless authentication
* OAuth2 login with Google
* OAuth2 login with GitHub
* Secure password hashing using BCrypt
* Role-Based Access Control (RBAC)
* Protected REST endpoints
* Custom authentication and authorization handling

### User Management

* User registration
* User login
* Role assignment
* Profile management
* Partial updates using PATCH APIs

### Teacher Management

* Create teacher records
* Update teacher information
* Delete teachers
* Fetch teacher details
* Assign teachers to classes

### Student Management

* Student registration
* Student profile management
* Student information updates
* Student search and retrieval

### Parent Management

* Parent registration
* Parent profile management
* Parent-student association

### Class Management

* Create classes
* Manage class information
* Teacher-class assignments
* Assignment validation

### Fee Management

* Configure fee structures
* Record fee payments
* Track payment history
* Payment status management

### Validation & Exception Handling

* Bean Validation (JSR-380)
* Custom business exceptions
* Global exception handling using @RestControllerAdvice
* Consistent API error responses

### Testing

* Unit testing using JUnit 5
* Mockito-based service testing
* Controller testing using MockMvc
* Authentication and security testing

---

## Architecture Highlights

* Layered Architecture
* DTO-based Request/Response Handling
* Separation of Concerns
* Repository Pattern
* Global Exception Handling
* JWT Authentication
* OAuth2 Authentication
* Environment Variable Configuration
* Dockerized Deployment
* Unit & Integration Testing

---

## Tech Stack

| Category         | Technology                |
| ---------------- | ------------------------- |
| Language         | Java 21                   |
| Framework        | Spring Boot 3             |
| Security         | Spring Security 6         |
| Authentication   | JWT                       |
| Social Login     | OAuth2 (Google, GitHub)   |
| ORM              | Spring Data JPA           |
| Database         | MySQL                     |
| Validation       | Bean Validation           |
| Testing          | JUnit 5, Mockito, MockMvc |
| Build Tool       | Maven                     |
| Containerization | Docker                    |

---

## Project Structure

```text
src/main/java/com/example/demo/

├── Controller/
├── Service/
├── Repository/
├── Entity/
├── DTO/
├── RequestDTO/
├── Security/
├── Helper/
└── MyException/
```

### Layer Responsibilities

| Layer       | Responsibility                     |
| ----------- | ---------------------------------- |
| Controller  | Handle HTTP requests and responses |
| Service     | Business logic                     |
| Repository  | Database access                    |
| Entity      | JPA entities                       |
| DTO         | API response models                |
| RequestDTO  | API request models                 |
| Security    | JWT and OAuth2 configuration       |
| Helper      | Utility classes and enums          |
| MyException | Custom exception classes           |

---

## Security

The application uses modern security practices:

* JWT Access Token Authentication
* Stateless Session Management
* OAuth2 Login with Google
* OAuth2 Login with GitHub
* BCrypt Password Encryption
* Endpoint Authorization
* Role-Based Access Control

### Supported Roles

```text
ADMIN
TEACHER
STUDENT
PARENT
```

---

## Database Design

### Core Entities

* User
* Student
* Teacher
* Parent
* SchoolClass
* Fee
* Payment

### Relationships

```text
Teacher  ---> SchoolClass

Parent   ---> Student

SchoolClass ---> Students

Student ---> Fee Records
```

---

## Environment Variables

Create a `.env` file in the project root:

```env
DB_URL=jdbc:mysql://localhost:3306/school_management
DB_USERNAME=root
DB_PASSWORD=password

JWT_SECRET=your-secret-key

GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret
```

---

## Getting Started

### Prerequisites

* Java 21+
* Maven 3.8+
* MySQL 8+
* Docker (Optional)

---

## Installation

### Clone Repository

```bash
git clone https://github.com/coder0030/school-management-system.git

cd school-management-system
```

### Build Project

```bash
mvn clean install
```

### Run Application

```bash
mvn spring-boot:run
```

Application starts on:

```text
http://localhost:8080
```

---

## Docker Setup

### Build Docker Image

```bash
docker build -t school-management-system .
```

### Run Docker Container

```bash
docker run -p 8080:8080 school-management-system
```

---

## API Modules

| Module           | Endpoint          |
| ---------------- | ----------------- |
| Authentication   | /auth/**          |
| Admin            | /admin/**         |
| Teacher          | /teacher/**       |
| Student          | /student/**       |
| Parent           | /parent/**        |
| Class Management | /class/**         |
| Class Teacher    | /class-teacher/** |
| Fee Management   | /fee/**           |

---

## Testing

### Run All Tests

```bash
mvn test
```

### Test Coverage Includes

* Service Layer Testing
* Controller Layer Testing
* Security Testing
* JWT Authentication Tests
* OAuth2 Authentication Tests
* Exception Handling Tests

---

## Future Enhancements

* Refresh Token Support
* Attendance Management
* Email Notifications
* Redis Caching
* Audit Logging
* API Rate Limiting
* CI/CD Pipeline with GitHub Actions
* Kubernetes Deployment
* Swagger/OpenAPI Documentation

---

## Learning Outcomes

This project helped strengthen practical experience in:

* Spring Boot
* Spring Security
* JWT Authentication
* OAuth2 Authentication
* REST API Development
* MySQL Database Design
* JPA/Hibernate
* Unit Testing
* Docker
* Clean Architecture
* Backend Development Best Practices

---

## Author

**Sumit Mishra**

Java Backend Developer

GitHub: https://github.com/coder0030

Email: [mishrasumit0530@gmail.com](mailto:mishrasumit0530@gmail.com)

---

## License

This project is open-source and available under the MIT License.
