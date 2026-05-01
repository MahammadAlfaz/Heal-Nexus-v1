# HealNexus Backend

A production-grade healthcare appointment management REST API built with Spring Boot 3, Spring Security 6, and MySQL. Supports role-based access for Patients, Doctors, and Admins with a complete appointment lifecycle, JWT authentication, and AOP-based audit logging.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Features](#features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [API Reference](#api-reference)
- [Appointment Lifecycle](#appointment-lifecycle)
- [Security Design](#security-design)
- [Audit Logging](#audit-logging)
- [Known Issues / Roadmap](#known-issues--roadmap)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| Security | Spring Security 6 (Stateless JWT) |
| Database | MySQL 8 |
| ORM | Spring Data JPA (Hibernate) |
| Mapping | ModelMapper |
| AOP | Spring AOP (AspectJ) |
| Build Tool | Maven |
| Validation | Jakarta Validation |

---

## Architecture

```
Controller → Service → Repository → Entity (MySQL)
```

Cross-cutting concerns are handled via:
- **Spring Security filter chain** — JWT validation on every request
- **AOP Aspect** — Audit logging on annotated service methods
- **Global Exception Handler** — Centralized error responses

```
Client
  │
  ├── JwtAuthenticationFilter        (validates Bearer token)
  │
  ├── SecurityConfig                 (route-level access rules)
  │
  ├── Controllers                    (REST endpoints)
  │     ├── AuthController
  │     ├── AppointmentController
  │     ├── DoctorController
  │     └── PatientController
  │
  ├── Services                       (business logic + @PreAuthorize)
  │     ├── AppointmentService
  │     ├── DoctorService
  │     ├── PatientService
  │     └── UserService
  │
  ├── AuditAspect                    (@Around audit logging)
  │
  └── Repositories                   (Spring Data JPA)
        ├── AppointmentRepository
        ├── DoctorRepository
        ├── PatientRepository
        └── UserRepository
```

---

## Features

### Authentication & Security
- JWT access tokens (15-minute expiry)
- HttpOnly cookie-based refresh tokens (7-day expiry) with token rotation
- BCrypt password encoding
- IP-based brute-force protection (blocks after 10 failed attempts for 1 minute)
- Account-level lockout after 20 failed login attempts (auto-unlocks after 15 minutes)
- Stateless session management
- Custom `AuthenticationEntryPoint` and `AccessDeniedHandler` for clean JSON error responses

### Role-Based Access Control
Three roles supported: `PATIENT`, `DOCTOR`, `ADMIN`

| Action | PATIENT | DOCTOR | ADMIN |
|---|---|---|---|
| Book appointment | ✅ | ❌ | ❌ |
| Cancel appointment | ✅ (own only) | ✅ (assigned only) | ✅ |
| Confirm appointment | ❌ | ✅ | ❌ |
| Complete appointment | ❌ | ✅ | ❌ |
| View own appointments | ✅ | ✅ | ❌ |

### Appointment Lifecycle
```
BOOKED → CONFIRMED → COMPLETED
          ↓              ↓
       CANCELLED      CANCELLED
```

### Profiles
- Patients and Doctors complete separate profile setup after registration
- Profile data is linked to the `User` entity via one-to-one relationship

### Pagination
- All list endpoints return paginated responses
- Max page size capped at 50
- Default sort by `appointmentTime` ascending
- Custom `PaginationResponse<T>` wrapper with metadata

### Audit Logging
- Custom `@Audit` annotation on service methods
- AOP aspect logs: user, role, action, method, entity ID, duration, timestamp, and success/failure status
- Structured log format for easy parsing by log aggregators (ELK, Datadog, etc.)

---

## Project Structure

```
src/
├── main/
│   ├── java/com/healnexus/
│   │   ├── HealNexusApplication.java
│   │   ├── audit/
│   │   │   ├── Audit.java                  (custom annotation)
│   │   │   └── AuditAspect.java            (AOP around advice)
│   │   ├── config/
│   │   │   ├── AppConfig.java              (enables AspectJ proxy)
│   │   │   ├── CorsConfig.java
│   │   │   └── ModelMapperConfig.java
│   │   ├── controller/
│   │   │   ├── AppointmentController.java
│   │   │   ├── AuthController.java
│   │   │   ├── DoctorController.java
│   │   │   └── PatientController.java
│   │   ├── dto/
│   │   │   ├── request/                    (input DTOs with validation)
│   │   │   └── response/                   (output DTOs)
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ResourceNotFoundException.java
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   ├── Doctor.java
│   │   │   ├── Patient.java
│   │   │   ├── Appointment.java
│   │   │   ├── AppointmentStatus.java
│   │   │   └── Role.java
│   │   ├── repositories/
│   │   │   ├── AppointmentRepository.java
│   │   │   ├── DoctorRepository.java
│   │   │   ├── PatientRepository.java
│   │   │   └── UserRepository.java
│   │   ├── security/
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── LoginAttemptService.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── SecurityUtils.java
│   │   │   ├── exception/
│   │   │   │   ├── CustomAccessDeniedHandler.java
│   │   │   │   └── CustomAuthenticationEntryPoint.java
│   │   │   ├── jwt/
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── JwtService.java
│   │   │   └── refresh/
│   │   │       ├── RefreshToken.java
│   │   │       ├── RefreshTokenRepository.java
│   │   │       └── RefreshTokenService.java
│   │   ├── service/
│   │   │   ├── AppointmentService.java
│   │   │   ├── DoctorService.java
│   │   │   ├── PatientService.java
│   │   │   └── UserService.java
│   │   └── util/
│   │       └── PaginationHelper.java
│   └── resources/
│       └── application.yaml
```

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- MySQL 8+

### 1. Clone the repository

```bash
git clone https://github.com/MahammadAlfaz/healnexus-backend.git
cd healnexus-backend
```

### 2. Create the MySQL database

```sql
CREATE DATABASE heal_nexus;
```

### 3. Configure environment variables

Create a `.env` file or set the following as environment variables (see [Environment Variables](#environment-variables) section).

Do **not** hardcode secrets in `application.yaml`.

### 4. Run the application

```bash
mvn spring-boot:run
```

The server starts on `http://localhost:8080`.

---

## Environment Variables

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | MySQL JDBC URL | `jdbc:mysql://localhost:3306/heal_nexus` |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | `yourpassword` |
| `JWT_SECRET` | HS256 signing key (min 32 chars) | `your-very-secure-secret-key-here` |

Update `application.yaml` to reference these:

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
```

---

## API Reference

### Auth

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/users/register` | Public | Register a new user |
| POST | `/api/users/login` | Public | Login, returns JWT + sets refresh cookie |
| POST | `/api/users/refresh` | Public | Refresh access token using cookie |
| POST | `/api/users/logout` | Public | Clears refresh token cookie |

### Appointments

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/appointments` | PATIENT | Book an appointment |
| PUT | `/api/appointments/{id}/cancel` | PATIENT, DOCTOR, ADMIN | Cancel an appointment |
| PUT | `/api/appointments/{id}/confirm` | DOCTOR | Confirm a booked appointment |
| PUT | `/api/appointments/{id}/complete` | DOCTOR | Mark appointment as completed |
| GET | `/api/appointments/patient/me` | PATIENT | Get current patient's appointments |
| GET | `/api/appointments/doctor/me` | DOCTOR | Get current doctor's appointments |

### Doctors

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/doctors/profile` | Authenticated | Complete doctor profile setup |
| GET | `/api/doctors/profile/{userId}` | Authenticated | Get doctor profile by user ID |

### Patients

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/patients/profile` | Authenticated | Complete patient profile setup |
| GET | `/api/patients/profile/{userId}` | Authenticated | Get patient profile by user ID |

---

### Request & Response Examples

**Register**
```json
POST /api/users/register
{
  "email": "patient@example.com",
  "password": "securepass",
  "role": "PATIENT"
}
```

**Login**
```json
POST /api/users/login
{
  "email": "patient@example.com",
  "password": "securepass"
}

Response:
{
  "message": "Login Sucessfull",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Book Appointment**
```json
POST /api/appointments
Authorization: Bearer <token>
{
  "doctorId": 1,
  "appointmentTime": "2025-06-15T10:00:00",
  "reason": "Routine checkup"
}
```

**Paginated Appointment Response**
```json
{
  "data": [ ... ],
  "page": 0,
  "size": 10,
  "totalElements": 25,
  "totalPages": 3,
  "hasNext": true,
  "hasPrevious": false,
  "first": true,
  "last": false
}
```

---

## Appointment Lifecycle

```
Patient books     →  BOOKED
Doctor confirms   →  CONFIRMED
Doctor completes  →  COMPLETED

Any party cancels →  CANCELLED  (from BOOKED or CONFIRMED)
```

**Business rules:**
- A patient can only book if their profile is active
- A doctor can only be booked if their profile is active
- Double-booking is prevented — a doctor cannot have two non-cancelled appointments at the same time slot
- Only the patient who owns an appointment can cancel it (Patients)
- Only the doctor assigned to an appointment can confirm/complete/cancel it (Doctors)
- Admins can cancel any appointment
- A COMPLETED or CANCELLED appointment cannot be cancelled again

---

## Security Design

### JWT Flow
```
Login → Access Token (15 min, in response body)
                    + Refresh Token (7 days, HttpOnly cookie)

Authenticated request → Bearer <access_token> in Authorization header

Token expired → POST /api/users/refresh (uses cookie automatically)
             → New access token + rotated refresh token
```

### Brute Force Protection
Two independent layers:

1. **IP-level** (`LoginAttemptService`): In-memory tracking. Blocks the IP after 10 failures for 1 minute. Resets on successful login.
2. **Account-level** (`UserService`): Persisted in DB. Locks the account after 20 failures for 15 minutes.

---

## Audit Logging

Annotate any service method with `@Audit(action = "ACTION_NAME")`:

```java
@Audit(action = "BOOK_APPOINTMENT")
public AppointmentResponse bookAppointment(...) { ... }
```

Log output format:
```
event=AUDIT status=SUCCESS user=patient@example.com role=PATIENT 
action=BOOK_APPOINTMENT method=bookAppointment entityId=1 
durationMs=45 timestamp=2025-05-01T10:30:00
```

Fields logged: `event`, `status`, `user`, `role`, `action`, `method`, `entityId`, `durationMs`, `timestamp`

---

argumentException` for duplicate patient instead of `IllegalStateException`

### Planned Features
- [ ] Doctor availability scheduling (working hours + slot generation)
- [ ] Leave/holiday management for doctors
- [ ] Digital prescription management
- [ ] Email notifications (appointment booked, confirmed, reminder)
- [ ] Patient medical history timeline
- [ ] Doctor ratings and reviews (post completed appointment)
- [ ] Admin dashboard analytics
- [ ] Audit log persistence to database
- [ ] Optimistic locking on Appointment entity
- [ ] Python AI microservice (FastAPI + LangGraph) for smart booking and health queries
- [ ] Flyway database migrations
- [ ] Integration tests

---

## Author

**Mahammad Alfaz**
- GitHub: [@MahammadAlfaz](https://github.com/MahammadAlfaz)
- LinkedIn: [mahammad-alfaz](https://www.linkedin.com/in/mahammad-alfaz-b27b3225a)

---

## License

This project is for educational and portfolio purposes.
