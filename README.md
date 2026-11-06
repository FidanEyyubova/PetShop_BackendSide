# PetShop Backend

REST API for **PetShop**, an online marketplace for pet owners. Customers buy pet products from independent
stores and find grooming, veterinary and training centers. Vendors run their own stores, service providers present
their centers, and administrators approve sellers and manage the site.

- **Live website:** https://pet-shop-frontend-side.vercel.app
- **API documentation (Swagger UI):** `http://localhost:8080/swagger-ui/index.html` when running locally

## Tech stack

| Area | Technology |
| --- | --- |
| Language / framework | Java 17, Spring Boot 3.2 |
| Database | PostgreSQL, Spring Data JPA (Hibernate) |
| Schema versioning | Liquibase (`ddl-auto: validate`; only Liquibase changes the schema) |
| Security | Spring Security, stateless JWT access tokens, rotating refresh tokens, role-based access |
| Validation / errors | Jakarta Bean Validation, global exception handler with one response format |
| Documentation | springdoc-openapi (Swagger UI) |
| Tests | JUnit 5, MockMvc, Testcontainers |
| Delivery | Docker, Docker Compose, Kubernetes manifests |

## Features

- **Storefront:** product catalogue with search, filters, sorting and facets, best sellers, stores, service providers, blog
- **Buyer:** cart, wishlist, checkout (one order per store, server-side prices, stock reservation), order tracking and cancellation, addresses, reviews
- **Vendor:** own store, products and stock, order processing (pending → processing → shipping → delivered), sales dashboard, finance and payouts
- **Service provider:** center profile, price list, team, weekly schedule, gallery, visitor metrics
- **Admin:** approval of vendors and service providers, catalogue and site content, orders, payouts, coupons, review moderation
- **Accounts:** email/password and Google sign-in, password reset by emailed code, newsletter emails

## Roles

| Role | Can do |
| --- | --- |
| `BUYER` | Shop, check out, track orders, write reviews |
| `VENDOR` | Manage own store, products, orders and payouts |
| `SERVICE_PROVIDER` | Manage own center, services, team and schedule |
| `ADMIN` | Approve sellers, manage content, oversee orders and payouts |

Vendors and service providers start as `PENDING_APPROVAL`: they can sign in and prepare their store or center,
but can publish only after an admin approves them.

## Project structure

```
src/main/java/com/petshop/backend
├── config/        application settings, OpenAPI/Swagger, data seeding
├── controller/    REST controllers (storefront, vendor, provider, admin)
├── service/       business logic
├── repository/    Spring Data JPA repositories and specifications
├── entity/        JPA entities
├── dto/           request and response objects
├── security/      JWT, filters, access rules, rate limiting
├── exception/     custom exceptions and the global exception handler
├── common/        enums and shared constants
└── util/          helpers
src/main/resources
├── application.yml
└── db/changelog/  Liquibase changelogs
src/test/java      unit tests and integration tests
k8s/               Kubernetes manifests
```

## Run locally

### Option 1: Docker Compose (backend + PostgreSQL)

Requirements: Docker Desktop.

```bash
cp .env.example .env        # optional: set ADMIN_EMAIL, ADMIN_PASSWORD, JWT_SECRET, SMTP, Google
docker compose up --build
```

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- PostgreSQL: `localhost:5433`, database `petshop_db`, user/password `postgres` / `postgres`

Stop with `docker compose down` (add `-v` to delete the database volume).

### Option 2: Maven with a local PostgreSQL

Requirements: Java 17 and PostgreSQL running on `localhost:5432`.

1. Create the database: `CREATE DATABASE petshop_db;`
2. Start the application (the database user and password default to `postgres` / `postgres`):

   ```bash
   # macOS / Linux
   ./mvnw spring-boot:run
   # Windows
   mvnw.cmd spring-boot:run
   ```

3. Liquibase creates all tables on the first start. Open http://localhost:8080/swagger-ui/index.html.

To create an admin account, set `ADMIN_EMAIL` and `ADMIN_PASSWORD` before starting:

```bash
# PowerShell
$env:ADMIN_EMAIL="admin@petshop.az"; $env:ADMIN_PASSWORD="ChangeMe123"; .\mvnw.cmd spring-boot:run
# cmd
set "ADMIN_EMAIL=admin@petshop.az" && set "ADMIN_PASSWORD=ChangeMe123" && mvnw.cmd spring-boot:run
# macOS / Linux
ADMIN_EMAIL=admin@petshop.az ADMIN_PASSWORD=ChangeMe123 ./mvnw spring-boot:run
```

## Configuration

All settings are environment variables; only the database is needed to start.

| Variable | Default | Purpose |
| --- | --- | --- |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/petshop_db` | Database URL |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | `postgres` / `postgres` | Database credentials |
| `PORT` | `8080` | HTTP port |
| `JWT_SECRET` | random per start | Token signing key, at least 64 characters. Set it in production so tokens survive restarts. |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | – | Admin account created on start |
| `CORS_ALLOWED_ORIGINS` | live website, `localhost:5173`, `localhost:3000` | Allowed frontend origins |
| `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD` | – | SMTP for emails. Empty `MAIL_HOST` = emails are only logged. |
| `MAIL_FROM`, `MAIL_NOTIFY_TO` | – | Sender address; inbox notified about new newsletter subscribers |
| `GOOGLE_CLIENT_ID` | – | Google sign-in. Empty = Google sign-in disabled. |
| `STORAGE_TYPE` | `local` | `local` (disk, `uploads/`) or `s3` (S3-compatible storage, `S3_*` variables) |
| `SHIPPING_FREE_THRESHOLD` / `SHIPPING_FEE` | `50.00` / `5.00` | Shipping rules (AZN) |
| `COMMISSION_RATE` | `0.10` | Platform commission on vendor sales |

Never commit real passwords, App Passwords or secrets; pass them as environment variables.

## Tests

```bash
./mvnw test          # Windows: mvnw.cmd test
```

- **Unit tests** (`DomainRulesTest`): business rules without a database (order status transitions, money, slugs, validation helpers).
- **Integration tests** (`src/test/java/.../integration`): the full application with MockMvc, Liquibase migrations and a real PostgreSQL database.
  - `AuthFlowIntegrationTest`: registration, login, refresh-token rotation and reuse detection, validation errors, password reset
  - `CheckoutFlowIntegrationTest`: cart → checkout → stock reservation → order lifecycle, insufficient stock, cancellation returns stock, ownership checks
  - `SellerApprovalIntegrationTest`: pending vendors and providers, admin approval, suspension
  - `SecurityIntegrationTest`: public, signed-in and role-restricted endpoints

With Docker running, the integration tests start a throw-away PostgreSQL container (Testcontainers).
Without Docker they use a separate local database `petshop_test` (created automatically; override with
`TEST_DB_URL`, `TEST_DB_USERNAME`, `TEST_DB_PASSWORD`). The application database is never used by tests.

## Docker

```bash
docker build -t petshop-backend:latest .
docker run -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/petshop_db petshop-backend:latest
```

The image is built in two stages (Maven build, then a JRE-only runtime) and runs as a non-root user.

## Kubernetes

Manifests in [`k8s/`](k8s):

| File | Contents |
| --- | --- |
| `namespace.yaml` | `petshop` namespace |
| `configmap.yaml` | Non-secret settings (database URL, CORS, shipping, storage) |
| `secret.yaml` | Database password, JWT secret, admin and SMTP credentials (placeholders) |
| `postgres.yaml` | PostgreSQL StatefulSet with a persistent volume and an internal Service |
| `backend.yaml` | Backend Deployment with startup/readiness/liveness probes and resource limits, uploads volume, Service |
| `ingress.yaml` | Ingress that exposes the API at `api.petshop.local` |
| `kustomization.yaml` | Applies all of the above in order |

Deploy (for example Docker Desktop Kubernetes or minikube):

```bash
# 1. Build the image where the cluster can see it
docker build -t petshop-backend:latest .        # minikube: run `eval $(minikube docker-env)` first

# 2. Replace the placeholder values in k8s/secret.yaml, then apply everything
kubectl apply -k k8s/

# 3. Watch the pods start (Liquibase migrates the database on the first start)
kubectl -n petshop get pods -w

# 4. Open the API
kubectl -n petshop port-forward svc/petshop-backend 8080:80
# http://localhost:8080/swagger-ui/index.html
```

With an NGINX ingress controller installed, add `127.0.0.1 api.petshop.local` to the hosts file and open
http://api.petshop.local/swagger-ui/index.html.

Health endpoints used by the probes: `/actuator/health/liveness` and `/actuator/health/readiness`.

## API overview

All responses share one envelope:

```json
{ "success": true, "message": "Products fetched", "data": { }, "code": null }
```

Errors carry a machine-readable `code` (for example `VALIDATION_ERROR`, `STOCK_PROBLEM`, `ACCOUNT_PENDING_APPROVAL`).
Lists are paged with `content`, `pageNumber`, `pageSize`, `totalElements`, `totalPages` and `last`.

| Status | Meaning |
| --- | --- |
| `200` / `201` | Success / created |
| `400` | Invalid input; field errors are listed in `data` |
| `401` | Not signed in, token expired or wrong credentials |
| `403` | Wrong role, not the owner, or account waiting for approval |
| `404` | Resource not found |
| `409` | Conflict: duplicate, not enough stock |
| `429` | Too many requests |

### Languages (az / en / ru)

Azerbaijani is the main language. Send `?lang=en|ru|az` or an `Accept-Language` header; without either the API answers in Azerbaijani.

- **Content** entered by admins, vendors and providers (products, categories, stores, blog, site sections, services, team, gallery) is returned in the requested language. A field with no translation falls back to the Azerbaijani text.
- **Writing:** the plain fields carry the Azerbaijani text, and `translations` carries the other two languages:

  ```json
  { "title": "İt yemi", "translations": { "en": { "title": "Dog food" }, "ru": { "title": "Корм для собак" } } }
  ```

  On an update, a language left out of `translations` keeps its stored text; send `"ru": {}` to clear it.
- **Edit forms:** detail responses include `translations` with all three languages (`az`, `en`, `ru`).
- **Messages:** error and validation messages, pet type / service type labels (`label`) and dashboard chart labels follow the requested language.

The full, interactive reference is in Swagger UI; exported specs are in [`docs/openapi.yaml`](docs/openapi.yaml) and [`docs/openapi.json`](docs/openapi.json).
