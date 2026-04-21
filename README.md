# Service Availability Monitor

A multi-tenant backend system for monitoring HTTP and TCP service availability in real time.
Built with Spring Boot, RabbitMQ, Redis, and PostgreSQL.

---

## Features

- Multi-tenant architecture — organizations can monitor their own services independently
- HTTP and TCP endpoint monitoring with configurable check intervals per service
- Real-time uptime metrics and response time tracking
- Event-driven email alerting via RabbitMQ when service status changes
- Redis caching for service state and notification rate limiting to prevent alert spam
- JWT authentication with login brute force protection
- Role-based access control — platform admins and tenant members

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 + Spring Boot 3 | Core framework |
| PostgreSQL | Primary database |
| Redis | Service state caching and rate limiting |
| RabbitMQ | Async notification pipeline |
| Flyway | Database migrations |
| Docker | Containerization |
| JWT | Authentication |
| JavaMail | Email notifications |

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                   REST API Layer                     │
│         (Auth, Tenants, Services, Checks)            │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│              Service Monitor Engine                  │
│                                                      │
│   ┌─────────────┐         ┌─────────────┐           │
│   │ HttpChecker │         │  TcpChecker │           │
│   └──────┬──────┘         └──────┬──────┘           │
│          └──────────┬────────────┘                  │
│                     │                               │
│          ┌──────────▼──────────┐                    │
│          │ ServiceMonitorEngine│                    │
│          │  (ThreadPoolScheduler)                   │
│          └──────────┬──────────┘                    │
└─────────────────────┼───────────────────────────────┘
                      │
         ┌────────────▼────────────┐
         │        RabbitMQ         │
         │   notification.queue    │
         └────────────┬────────────┘
                      │
         ┌────────────▼────────────┐
         │   NotificationConsumer  │
         │     → EmailService      │
         └─────────────────────────┘
```

---

## Prerequisites

- Docker and Docker Compose
- Gmail account for email notifications (or any SMTP provider)

---

## Getting Started

**1. Clone the repository:**

```bash
git clone https://github.com/yourusername/service-availability-monitor.git
cd service-availability-monitor
```

**2. Create a `.env` file in the root directory:**

```bash
JWT_SECRET=your-base64-encoded-secret-here
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-gmail-app-password
MAIL_FROM=your-email@gmail.com
```

> For Gmail, use an [App Password](https://support.google.com/accounts/answer/185833), not your regular password.

**3. Run with Docker Compose:**

```bash
docker-compose up --build
```

The app will be available at `http://localhost:8080/api/v1`

---

## API Documentation

Interactive API documentation is available at:
`http://localhost:8080/swagger-ui.html`

### Key Endpoints

**Auth:**
```
POST /api/v1/auth/register   - Register a new user
POST /api/v1/auth/login      - Login and receive JWT token
```

**Tenants:**
```
POST   /api/v1/tenants       - Create a tenant
GET    /api/v1/tenants       - Get all tenants
GET    /api/v1/tenants/{id}  - Get a tenant by id
```

**Monitored Services:**
```
POST   /api/v1/services                   - Add a service to monitor
GET    /api/v1/services/tenant/{tenantId} - Get all services for a tenant
GET    /api/v1/services/{id}              - Get a service by id
PUT    /api/v1/services/{id}              - Update a service
DELETE /api/v1/services/{id}              - Delete a service
```

**Check History:**
```
GET /api/v1/checks/service/{serviceId}          - Full check history
GET /api/v1/checks/service/{serviceId}/latest   - Most recent check
GET /api/v1/checks/service/{serviceId}/stats    - Uptime stats
```

### Authentication

All endpoints except `/auth/**` require a JWT token:

```
Authorization: Bearer your-jwt-token-here
```

---

## How It Works

1. **Register** and **login** to receive a JWT token
2. **Create a tenant** — your organization
3. **Add services** to monitor with a name, URL, type (HTTP/TCP), and check interval
4. The **monitoring engine** checks each service at its configured interval using a thread pool
5. Each check result is recorded — status, response time, status code, errors
6. When a service **changes status** (UP → DOWN or DOWN → UP), an event is published to RabbitMQ
7. The **notification consumer** picks up the event and sends an email alert
8. **Redis** caches the last known status and rate limits notifications to prevent spam

---

## Management UIs

| Service | URL | Credentials |
|---|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html | JWT token |
| RabbitMQ UI | http://localhost:15672 | guest / guest |

---

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/monitor` |
| `DB_USERNAME` | Database username | `monitor` |
| `DB_PASSWORD` | Database password | `monitor` |
| `JWT_SECRET` | Base64 encoded JWT secret | required |
| `JWT_EXPIRATION` | JWT expiry in milliseconds | `86400000` (24h) |
| `RABBITMQ_HOST` | RabbitMQ host | `localhost` |
| `RABBITMQ_USERNAME` | RabbitMQ username | `guest` |
| `RABBITMQ_PASSWORD` | RabbitMQ password | `guest` |
| `REDIS_HOST` | Redis host | `localhost` |
| `MAIL_USERNAME` | SMTP email address | required |
| `MAIL_PASSWORD` | SMTP password or app password | required |
| `MAIL_FROM` | From email address | required |

---

## Project Structure

```
src/main/java/com/nzube/service_availability_monitor/
├── checker/          - HTTP and TCP checkers
├── config/           - Spring configuration classes
├── controller/       - REST controllers
├── dto/              - Request and response objects
├── entity/           - JPA entities
├── enums/            - Enumerations
├── exception/        - Custom exceptions and global handler
├── mapper/           - Entity to DTO mappers
├── notification/     - RabbitMQ publisher and consumer
├── repository/       - Spring Data JPA repositories
├── scheduler/        - Dynamic service monitor scheduler
├── security/         - JWT filter, util and security config
└── service/          - Business logic
```

---

## License

MIT