# Heal-Nexus-v1

HealNexus is a production-style backend system designed for managing healthcare workflows including patients, doctors, and appointments with secure access control and scalable architecture.

---

## 🚀 Tech Stack

- Java 21
- Spring Boot 3
- Spring Security 6 (JWT Authentication)
- Spring Data JPA
- MySQL
- Lombok
- ModelMapper
- Maven

---

## 🔐 Key Features

### 👤 Authentication & Security
- JWT-based authentication (stateless)
- Role-based access control (PATIENT, DOCTOR, ADMIN)
- Custom AuthenticationEntryPoint (401 handling)
- AccessDeniedHandler (403 handling)
- Method-level security using `@PreAuthorize`

---

### 🏥 User & Role Management
- Separate roles for Patient, Doctor, Admin
- Secure registration & login APIs
- DTO-based architecture (no direct entity exposure)

---

### 📅 Appointment Management System
- Full lifecycle management:
  - BOOKED → CONFIRMED → COMPLETED
  - Cancellation supported with validation
- Strict state transition validation
- Ownership checks for secure access

---

### 📊 Pagination & API Design
- Offset-based pagination using Pageable
- Custom `PaginationResponse` wrapper
- Max page size limit (50)
- Clean REST API design

---

### 🛡️ Production-Ready Features
- Global exception handling
- Input validation
- AOP-based audit logging
- Layered architecture (Controller → Service → Repository)

---

## 🧠 Architecture Overview

- Controller Layer → Handles HTTP requests
- Service Layer → Business logic & validation
- Repository Layer → Database interaction
- Security Layer → JWT filter + role-based access

---

## 📂 Project Structure

The project follows a layered architecture to ensure separation of concerns and maintainability:

- **controller/** → Exposes REST APIs and handles HTTP requests/responses  
- **service/** → Contains core business logic and validation rules  
- **repository/** → Handles database interactions using Spring Data JPA  
- **dto/** → Defines request and response models (avoids exposing entities)  
- **entity/** → Represents database tables and domain models  
- **security/** → JWT authentication, filters, and authorization logic  
- **exception/** → Centralized exception handling and error responses  
- **config/** → Application-level configurations (e.g., ModelMapper, security setup)

## ⚙️ How to Run

### 1️⃣ Clone the repository
```bash
git clone https://github.com/MahammadAlfaz/HealNexus
cd HealNexus
```
2️⃣ Configure Database
Update application.properties:
Copy code
Properties
```bash
spring.datasource.url=jdbc:mysql://localhost:3306/healnexus
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

3️⃣ Run the Application
Using Maven:
Copy code
```
mvn spring-boot:run
```
Or run the main class from your IDE.


🔗 API Overview

🔐 Auth APIs
POST /api/auth/register
POST /api/auth/login

👤 User APIs
Manage users (Patient / Doctor / Admin)
Role-based access control using @PreAuthorize

📅 Appointment APIs
Book appointment
Confirm appointment
Complete appointment
Cancel appointment
👉 Enforces strict state transitions:
Copy code

BOOKED → CONFIRMED → COMPLETED

🧪 Testing
Tested using Postman
Includes:
Auth flows (JWT)
Role-based access testing
Appointment lifecycle validation.

📊 Pagination
Implemented using Spring Pageable
Custom response wrapper:
PaginationResponse<T>
Max page size limit: 50

🛡️ Security Highlights
Stateless JWT authentication
Custom filter: JwtAuthenticationFilter
Role-based authorization
Ownership validation (users can only access their data)


📌 Future Improvements
Doctor availability scheduling
Integration & unit testing
Role hierarchy (ADMIN > DOCTOR > PATIENT)
Optimistic locking for concurrency handling
API response standardization


👨‍💻 Author
Mahammad Alfaz
GitHub: https://github.com/MahammadAlfaz
LinkedIn: https://www.linkedin.com/in/mahammad-alfaz-b27b3225a
