# LoanSync

An enterprise-style lending platform, built module by module as independent Spring Boot services. This project exists to actually build (not just describe) the kind of system outlined in the original architecture — real code, real schema, real tests.

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.16 |
| Build tool | Maven 3.8.8 |
| ORM | Spring Data JPA / Hibernate |
| Database | PostgreSQL 16 |
| Migrations | Flyway (added in Phase 2 of each module) |
| Auth | Spring Security 6 + JWT (jjwt 0.13.0) |
| Messaging | Kafka (introduced once 2+ services need to talk async) |
| Docs | Springdoc OpenAPI / Swagger |
| Containers | Docker (via WSL, no Docker Desktop) |
| Testing | JUnit 5, Mockito, Postman for manual API testing |

## How we're building this

Every module goes through the same three phases before it's considered "done." This is deliberate — it means each layer can be tested and understood in isolation instead of debugging five new concepts at once.

**Phase 1 — Bare CRUD, no auth, no Flyway**
Entity → DTO → Repository → Service → Controller. Run with `spring.jpa.hibernate.ddl-auto=update` just to get moving fast. Test every endpoint manually in Postman. No security filters, no migrations yet — the goal is proving the business logic and API shape are right before locking anything in.

**Phase 2 — Flyway**
Once the schema stops changing shape every five minutes, freeze it: turn `ddl-auto` to `validate`, write the `V1__init_*.sql` migration by hand from the final entity design, and let Flyway own the schema from here on. Any future schema change becomes a new versioned migration file, never a hand-edited table.

**Phase 3 — Security**
Add Spring Security + JWT last, once the endpoints already work. Wrap existing endpoints with role checks (`ADMIN`, `LENDER`, `BORROWER`), wire in the JWT filter, and re-test the same Postman collection — this time with a token.

Later, cross-cutting phases apply once multiple services exist:

**Phase 4 — Inter-service communication** (Kafka events, e.g. loan-approved → trigger disbursement)
**Phase 5 — Containerization** (Dockerfile per service + docker-compose for local multi-service runs)
**Phase 6 — Gateway & discovery** (Spring Cloud Gateway once 3+ services exist to route through)

---

## Module Build Order

```
1. user-service                      ← current focus
2. loan-origination-service
3. loan-processing-service
4. payment-service
5. loan-servicing-service
6. notification-service
7. reporting-service
8. audit-service
9. admin-service
10. api-gateway (cross-cutting, Phase 6)
```

---

## Module Breakdown

### 1. User Management (`user-service`)
**Purpose**: registration, authentication, authorization — everything else depends on this.

- Core entities: `User`, `Role` (`ADMIN`, `LENDER`, `BORROWER`), `user_roles` join table
- Phase 1: register/login as plain CRUD (no password hashing gate yet if you want, or add BCrypt early since it's cheap) — `POST /users`, `GET /users/{id}`, `GET /users`
- Phase 2: Flyway migration for `users`, `roles`, `user_roles`, seed the 3 roles
- Phase 3: Spring Security + JWT (access + refresh tokens), role-based endpoint locking
- Later: email verification, password reset, refresh-token rotation

### 2. Loan Origination (`loan-origination-service`)
**Purpose**: intake and approval of new loan applications.

- Core entities: `LoanApplication` (applicant ref, amount, tenure, purpose, status), `CreditCheckResult`
- Phase 1: submit application, fetch by id, list by user, basic status transitions (`SUBMITTED` → `UNDER_REVIEW` → `APPROVED`/`REJECTED`)
- Phase 2: Flyway schema, add constraints (amount > 0, tenure within allowed range)
- Phase 3: lock submission to `BORROWER` role, approval actions to `LENDER`/`ADMIN`
- Later: multi-step approval workflow, third-party credit bureau integration, async credit-check via Kafka

### 3. Loan Processing (`loan-processing-service`)
**Purpose**: disbursement and repayment schedule generation once a loan is approved.

- Core entities: `Loan` (linked to approved application), `RepaymentSchedule`, `EmiInstallment`
- Phase 1: generate amortization schedule given principal/rate/tenure, disbursement record creation
- Phase 2: Flyway schema, indexing on loan_id for fast schedule lookups
- Phase 3: restrict disbursement trigger to internal/admin roles
- Later: Spring Batch/Quartz for scheduled EMI generation, interest recalculation on restructuring

### 4. Payment Management (`payment-service`)
**Purpose**: all payment transactions against a loan.

- Core entities: `Transaction` (amount, method, status), `PaymentReconciliation`
- Phase 1: record a payment, fetch transaction history, basic refund/partial-payment handling
- Phase 2: Flyway schema, `DataIntegrityViolationException` handling for duplicate transaction refs
- Phase 3: secure endpoints, audit trail (who initiated what)
- Later: real payment gateway integration (UPI/cards/net banking), Kafka-driven retry on failed transactions

### 5. Loan Servicing & Account Management (`loan-servicing-service`)
**Purpose**: ongoing account view for borrowers — statements, balances, restructuring.

- Core entities: `AccountStatement`, `LoanBalanceSnapshot`
- Phase 1: statement generation from existing loan/payment data, balance inquiry endpoint
- Phase 2: Flyway schema
- Phase 3: borrowers can only see their own account (ownership check, not just role check)
- Later: moratorium/restructuring workflows, customer support ticketing

### 6. Notification & Communication (`notification-service`)
**Purpose**: email/SMS/push for reminders, approvals, alerts.

- Core entities: `NotificationLog`, `NotificationTemplate`
- Phase 1: send a plain email on demand (JavaMail), log what was sent
- Phase 2: Flyway schema for templates and logs
- Phase 3: internal-service-only auth (this service is called by other services, not end users directly)
- Later: SMS (Twilio), push (Firebase), Kafka consumer that reacts to `loan-approved`/`payment-due` events

### 7. Reporting & Analytics (`reporting-service`)
**Purpose**: dashboards and reports for stakeholders.

- Core entities: mostly read models / aggregation queries over other services' data
- Phase 1: a couple of hardcoded report endpoints (loans by status, total disbursed this month)
- Phase 2: Flyway if this service keeps its own aggregate tables; otherwise read-only against other schemas
- Phase 3: restrict to `ADMIN`/`LENDER`
- Later: JasperReports exports, scheduled report generation

### 8. Audit & Compliance (`audit-service`)
**Purpose**: traceability of every sensitive action across the system.

- Core entities: `AuditEntry` (actor, action, entity, timestamp, before/after)
- Phase 1: expose a way to write and query audit entries manually
- Phase 2: Flyway schema, indexing on entity + timestamp for fast lookups
- Phase 3: write-only for services (via internal API key or service-to-service auth), read-only for `ADMIN`
- Later: Spring AOP to auto-log method calls, ELK stack integration

### 9. Admin & Configuration (`admin-service`)
**Purpose**: centralized config and admin controls.

- Core entities: `SystemConfig` (key-value, e.g. current interest rates, loan policy limits)
- Phase 1: CRUD on config values
- Phase 2: Flyway schema, seed default config values
- Phase 3: `ADMIN`-only, everywhere
- Later: Spring Boot Admin dashboard, health-check aggregation across all services

### 10. API Gateway (`api-gateway`)
**Purpose**: single entry point once enough services exist to make direct calls unwieldy.

- Not a CRUD service — no entities of its own
- Introduced in Phase 6: Spring Cloud Gateway routes, centralized JWT validation at the edge, rate limiting
- Only makes sense once 3+ services are running — building this first would just be routing to nothing

---

## Exception Handling Pattern (applies to every service)

Every service gets the same `GlobalExceptionHandler` shape via `@RestControllerAdvice`:

- Domain-specific exceptions (e.g. `LoanNotFoundException`, `InsufficientFundsException`, `InvalidLoanApplicationException`) → mapped to appropriate HTTP status (404, 402/409, 400)
- `MethodArgumentNotValidException` (from `@Valid`) → 400 with field-level error map
- `DataIntegrityViolationException` (DB constraint violations) → 409 with a clean message, log the raw exception
- `AccessDeniedException` → 403
- Catch-all `Exception` → 500 with a generic message, never leak stack traces to the client

---

## Current Status

- [x] Architecture and module breakdown finalized
- [ ] `user-service` — entity, DTO, exceptions, repository done → service impl next (Phase 1, no auth/Flyway yet)
- [ ] Everything else: not started

This file gets updated as each module moves through its phases.
