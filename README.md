# Library (library-962dc383)

Spring Boot 3 backend déployé sur AWS Lambda.
API de gestion de bibliothèque — succursales, catalogue, clients, ventes, réceptions de stock.
Authentification JWT avec contrôle d'accès par rôle (ADMIN / CUSTOMER).

## Prérequis

- Java 21
- PostgreSQL 14+
- Gradle (ou utiliser `./gradlew`)

## Configuration

### 1. Fichier `.env`

```bash
PGHOST=localhost
PGDATABASE=library
PGUSER=postgres
PGPASSWORD=postgres
PGSSLMODE=disable
```

> `.env` est gitignoré — vous pouvez y mettre vos vrais identifiants.

### 2. JWT (optionnel)

Par défaut, une clé HMAC-SHA256 est générée au démarrage. En production, définissez :

```bash
JWT_SECRET=base64-encoded-256-bit-key
```

### 3. Build & test

```bash
./gradlew build -x test   # compilation uniquement
./gradlew test            # tests unitaires + intégration (TestContainers spin up Postgres auto)
./gradlew bootRun         # démarre sur http://localhost:8080
```

## Endpoints

58 endpoints — spec OpenAPI 3.0.3 dans `doc/openapi.yml` (27 paths).

Groupes principaux :

| Groupe | Description |
|---|---|
| **Auth** | Inscription (`POST /register`) et connexion (`POST /login`) — token JWT |
| **Libraries** | CRUD succursales + recherche |
| **Books** | Catalogue — CRUD, filtre par auteur, genre |
| **Authors** | CRUD auteurs |
| **Genres** | CRUD genres |
| **Customers** | CRUD clients (lié à User) |
| **Users** | CRUD utilisateurs (admin) |
| **Book Copies** | Exemplaires par librairie + statut + stock |
| **Arrivals** | Réceptions de stock + lignes d'arrivage |
| **Sales** | Ventes et articles vendus |

> `GET /ping` — health check
> `GET /health/email` — statut email SES

Tous les endpoints retournent des réponses paginées (listes) avec `PageResponse<data>` + `PaginationDto` en meta.

**Authentification :** Les endpoints POST/PATCH/DELETE nécessitent un token JWT avec rôle `ADMIN`. Les endpoints GET sont publics. Passez le token dans l'en-tête `Authorization: Bearer <token>`.

## Stack

Java 21 · Spring Boot 3.2.2 · PostgreSQL · AWS Lambda/SQS/SES · Gradle 8.5 · Lombok · JWT (Argon2) · TestContainers · JaCoCo · GitHub Actions

## Architecture

```
JwtAuthenticationFilter (chaîne Spring Security)
         ↓
Controller (DTO) → Service (validation + orchestration) → Repository (entité JPA)
                                                               ↓
                                                          Mapper → DTO
```

- **Repository** : JPA `@Query` natives, retourne des entités, INSERT RETURNING pattern
- **Service** : valide via `DataValidator` + validators domaine, orchestre, mappe entité → DTO
- **Controller** : reçoit/renvoie des DTOs, aucun traitement métier

## Validation

Validation centralisée dans `DataValidator` (email, phone, ISBN, safe strings) + validateurs par domaine (`AuthorValidator`, `BookCopyValidator`, `SaleValidator`, etc.). Appelée dans le service — pas d'annotations JSR-380 sur les DTOs.

## Gestion des exceptions

Hiérarchie standardisée : `BadRequestException` (400), `UnauthorizedException` (401), `ForbiddenException` (403), `NotFoundException` (404), `ConflictException` (409), `UnprocessableEntityException` (422). Toutes gérées par `GlobalExceptionHandler` (`@RestControllerAdvice`) qui retourne des JSON `ErrorBody`.

---

*Projet généré via POJA — [hei.school](https://hei.school)*
