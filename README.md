# Library (library-962dc383)

Spring Boot 3 backend, deployed on AWS Lambda.

## Prerequisites

- Java 21
- PostgreSQL 14+
- Gradle (or use `./gradlew`)

## Setup

### 1. Configure `.env`

```bash
PGHOST=localhost
PGDATABASE=library
PGUSER=postgres
PGPASSWORD=postgres
PGSSLMODE=disable
```

> `.env` is gitignored — safe to put real credentials here.

### 2. Build & test

```bash
./gradlew build -x test   # compile only
./gradlew test            # full test suite (TestContainers spins up Postgres automatically)
./gradlew bootRun         # start on http://localhost:8080
```

## Endpoints

47 endpoints — spec complète au format OpenAPI 3.0.3 dans `doc/openapi.yml`.

Principaux groupes :
- **Libraries** — CRUD succursales
- **Books** — catalogue, auteurs, genres
- **Customers** — clients
- **Book Copies** — exemplaires par librairie
- **Arrivals** — réceptions de stock
- **Sales** — ventes et articles vendus

Tous les endpoints retournent des réponses paginées (listes) avec des exemples inline.

> `GET /ping` — health check
> `GET /health/email` — SES email status

## Stack

Java 21 · Spring Boot 3.2.2 · PostgreSQL · AWS Lambda/SQS/SES · Gradle 8.5 · Lombok · TestContainers · JaCoCo

---

*POJA-generated — [hei.school](https://hei.school)*
