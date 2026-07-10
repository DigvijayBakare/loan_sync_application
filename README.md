# LoanSync

LoanSync is a lending management platform built as independent microservices. It handles the full lifecycle of a loan — from a borrower submitting an application, through approval, disbursement, repayment, and servicing — the way a bank or NBFC's internal lending system would.

## What it does

- **Borrowers** can register, apply for a loan, track its approval status, view their repayment schedule, and make payments.
- **Lenders/Admins** can review and approve/reject applications, configure interest rates and loan policies, and view portfolio-level reports.
- The system generates amortization schedules, tracks every payment and transaction, sends notifications at key stages (approval, payment due, payment received), and keeps an audit trail of sensitive actions for compliance.

In short: it's the backend a fintech company would run to manage its lending business end-to-end, rather than a single monolithic loan calculator.

## Why microservices

Each part of the lending lifecycle — user identity, loan origination, disbursement/repayment, payments, servicing, notifications, reporting, audit — is built as its own deployable service with its own database. That means:

- A spike in payment traffic at month-end doesn't need scaling the whole system, just `payment-service`.
- Teams (or, here, future-you) can work on one service without breaking another.
- Each service can be tested, deployed, and rolled back independently.

## Architecture

```
                        ┌─────────────────┐
                        │   API Gateway    │   (routing, JWT validation at the edge)
                        └────────┬─────────┘
        ┌──────────────┬────────┼─────────┬──────────────┬───────────────┐
        │              │        │         │              │               │
  user-service   loan-origin  loan-proc  payment-svc  notification   reporting /
  (auth, roles)  -ation-svc   -essing-svc (transactions) -service    audit / admin
                 (applications)(disbursement,                        services
                                schedules)
```

Services communicate synchronously via REST for request/response flows (e.g. checking a user's role) and asynchronously via Kafka for events that shouldn't block the caller (e.g. "loan approved" triggering disbursement + a notification).

## Tech stack

| Layer | Technology |
|---|---|
| Language / Runtime | Java 17 |
| Framework | Spring Boot 3.5.16 |
| Persistence | Spring Data JPA (Hibernate) + PostgreSQL |
| Migrations | Flyway |
| Security | Spring Security 6 + JWT |
| Messaging | Kafka (async, cross-service events) |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Containers | Docker |
| Testing | JUnit 5, Mockito |

## Services

| Service | Responsibility |
|---|---|
| `user-service` | Registration, login, JWT issuance, role-based access (Admin / Lender / Borrower) |
| `loan-origination-service` | Loan application submission, eligibility checks, approval workflow |
| `loan-processing-service` | Disbursement and EMI/amortization schedule generation |
| `payment-service` | Payment transactions, reconciliation, refunds |
| `loan-servicing-service` | Account statements, balance inquiries, loan restructuring |
| `notification-service` | Email/SMS/push notifications for key loan events |
| `reporting-service` | Portfolio dashboards and compliance reports |
| `audit-service` | Audit trail for sensitive actions |
| `admin-service` | System configuration (interest rates, loan policies) |
| `api-gateway` | Single entry point, routing, edge auth |

## Getting started

Each service is a standalone Spring Boot app with its own `pom.xml` and database. To run a service locally:

```bash
# start Postgres (via WSL Docker, no Docker Desktop required)
docker compose up -d postgres

# build and run a given service, e.g. user-service
cd user-service
mvn clean install
mvn spring-boot:run
```

Once running, each service exposes interactive API docs at:
```
http://localhost:<port>/swagger-ui.html
```

## Status

This project is under active, incremental development — services are being built one at a time, each fully tested before the next begins.

- [x] `user-service` — in progress
- [ ] `loan-origination-service`
- [ ] `loan-processing-service`
- [ ] `payment-service`
- [ ] `loan-servicing-service`
- [ ] `notification-service`
- [ ] `reporting-service`
- [ ] `audit-service`
- [ ] `admin-service`
- [ ] `api-gateway`

## License

MIT (or update as needed)
