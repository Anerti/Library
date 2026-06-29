# Library (library-962dc383)

## Stack
- **Lang:** Java 21
- **Framework:** Spring Boot 3.2.2
- **Build:** Gradle 8.5
- **Database:** PostgreSQL (via RDS, `sslmode=require`)
- **Cloud:** AWS Lambda (deployed as `springboot3` container), SQS, EventBridge, SES
- **API:** OpenAPI 3.0.3 (hand-authored, 27 paths, 58 ops)
- **Auth:** JWT (bearer token) + Argon2 password encoding + role-based access (ADMIN / CUSTOMER)
- **Codegen:** `org.openapi.generator` 7.7.0 (generates Spring server stubs from spec)
- **Test:** JUnit 5 + TestContainers 2.0.2 + JUnit Pioneer + MockMvc
- **Coverage:** JaCoCo 0.8.11 (line coverage ≥ 0%, reports to XML + HTML)
- **Misc:** Lombok, Jackson, Apache Tika, Reflections, Jakarta Mail
- **CI:** GitHub Actions (CI on every push, CD Compute on preprod/prod via Poja API)

## Project structure
```
Library/
├── build.gradle                 # Build config, dependencies, JaCoCo rules
├── settings.gradle              # rootProject.name = 'library-962dc383'
├── format.sh                    # Code formatter
├── .env                         # DB creds (PGHOST, PGDATABASE, PGUSER, PGPASSWORD, PGSSLMODE)
├── banner.txt                   # Spring Boot ASCII art banner
├── .github/workflows/
│   ├── ci.yml                   # test + format on every push/PR
│   ├── cd-compute.yml           # deploy to AWS Lambda via Poja API (preprod/prod)
│   └── release-version.yml
├── doc/
│   ├── api.yml                  # API-specific OpenAPI spec
│   ├── library-mcd.canvas       # MCD conceptuel (Obsidian Canvas)
│   └── openapi.yml              # OpenAPI 3.0.3 spec (27 paths, 50 ops)
├── script/auth/                 # Auth test scripts
│   └── test_register.sh
└── src/
    ├── main/
    │   ├── java/hei/school/library/
    │   │   ├── PojaApplication.java             # @SpringBootApplication entry
    │   │   ├── PojaGenerated.java               # Marker annotation (generated code)
    │   │   ├── config/                          # Security config
    │   │   │   ├── JwtAuthenticationFilter.java # OncePerRequestFilter — extracts JWT, sets SecurityContext
    │   │   │   ├── JwtTokenProvider.java        # Token creation/validation (subject = user UUID)
    │   │   │   └── SecurityConfig.java          # Spring Security filter chain, endpoint auth rules
    │   │   ├── dto/                             # Request/Response DTOs (Lombok builders)
    │   │   │   ├── ArrivalItemRequest/Response.java
    │   │   │   ├── ArrivalRequest/Response.java
    │   │   │   ├── AuthResponse.java            # JWT token response
    │   │   │   ├── AuthorListResponse.java
    │   │   │   ├── AuthorRequest/Response/UpdateRequest.java
    │   │   │   ├── BookCopyRequest/Response/UpdateRequest.java
    │   │   │   ├── BookRequest/Response/UpdateRequest.java
    │   │   │   ├── CustomerRequest/Response/UpdateRequest.java
    │   │   │   ├── GenreListResponse.java
    │   │   │   ├── GenreRequest/Response/Summary.java
    │   │   │   ├── LibraryListResponse.java
    │   │   │   ├── LibraryRequest/Response.java
    │   │   │   ├── PageResponse.java + PaginationDto.java
    │   │   │   ├── SaleItemRequest/Response.java
    │   │   │   ├── SaleRequest/Response/UpdateRequest.java
    │   │   │   ├── UserRequest/Response.java
    │   │   │   └── UserUpdateRequest.java
    │   ├── endpoint/
    │   │   ├── EndpointConf.java
    │   │   ├── RequestLoggerConfigurer.java
    │   │   └── rest/controller/
    │   │       ├── health/
    │   │       │   ├── PingController.java
    │   │       │   └── HealthEmailController.java
    │   │       ├── ArrivalController.java
    │   │       ├── ArrivalItemController.java
    │   │       ├── AuthController.java      # POST /register, POST /login
    │   │       ├── AuthorController.java
    │   │       ├── BookController.java
    │   │       ├── BookCopyController.java
    │   │       ├── CustomerController.java
    │   │       ├── GenreController.java
    │   │       ├── GetStockBookCopyController.java
    │   │       ├── LibraryController.java
    │   │       ├── SaleController.java
    │   │       ├── SaleItemController.java
    │   │       └── UserController.java       # CRUD users (admin)
    │   ├── entity/                          # JPA entities
    │   │   ├── Arrival.java
    │   │   ├── ArrivalItem.java
    │   │   ├── Author.java
    │   │   ├── Book.java
    │   │   ├── BookCopy.java
    │   │   ├── Customer.java
    │   │   ├── Genre.java
    │   │   ├── Library.java
    │   │   ├── Sale.java
    │   │   ├── SaleItem.java
    │   │   ├── User.java                   # Replaces Customer — unified auth + profile
    │   │   └── enums/
    │   │       ├── BookCopyFormat.java
    │   │       ├── BookCopyStatus.java
    │   │       ├── Role.java               # ADMIN, CUSTOMER
    │   │       └── SaleStatus.java
    │   ├── exception/                       # Centralised exception handling
    │   │   ├── BadRequestException.java
    │   │   ├── ConflictException.java
    │   │   ├── ErrorBody.java
    │   │   ├── ForbiddenException.java      # 403 — insufficient role
    │   │   ├── GlobalExceptionHandler.java  # @RestControllerAdvice
    │   │   ├── NotFoundException.java
    │   │   ├── UnauthorizedException.java   # 401 — missing/invalid JWT
    │   │   └── UnprocessableEntityException.java
    │   ├── file/hash/                       # FileHasher
    │   ├── file/zip/                        # FileTyper
    │   ├── concurrency/                     # ThreadRenamer
    │   ├── datastructure/                   # ListGrouper
    │   ├── handler/LambdaHandler.java       # AWS Lambda entry
    │   ├── mail/                            # Email (Mailer, Email, EmailConf, EmailAddressVerifier)
    │   ├── mapper/                          # Entity ↔ DTO converters
    │   │   ├── ArrivalItemMapper.java
    │   │   ├── ArrivalMapper.java
    │   │   ├── AuthorMapper.java
    │   │   ├── BookCopyMapper.java
    │   │   ├── BookMapper.java
    │   │   ├── CustomerMapper.java
    │   │   ├── GenreMapper.java
    │   │   ├── LibraryMapper.java
    │   │   ├── PaginationMapper.java
    │   │   ├── SaleItemMapper.java
    │   │   ├── SaleMapper.java
    │   │   └── UserMapper.java            # User → UserResponse
    │   ├── repository/dao/                  # JPA repositories (INSERT RETURNING pattern)
    │   │   ├── ArrivalItemRepository.java
    │   │   ├── ArrivalRepository.java
    │   │   ├── AuthorRepository.java
    │   │   ├── AuthRepository.java        # Native queries for auth (register / login)
    │   │   ├── BookCopyRepository.java
    │   │   ├── BookRepository.java
    │   │   ├── CustomerRepository.java
    │   │   ├── GenreRepository.java
    │   │   ├── LibraryRepository.java
    │   │   ├── SaleItemRepository.java
    │   │   ├── SaleRepository.java
    │   │   └── UserRepository.java        # CRUD users
    │   ├── service/                         # Validation + orchestration
    │   │   ├── ArrivalItemService.java
    │   │   ├── ArrivalService.java
    │   │   ├── AuthService.java           # Register + login orchestration
    │   │   ├── AuthorService.java
    │   │   ├── BookCopyService.java
    │   │   ├── BookService.java
    │   │   ├── CustomerService.java
    │   │   ├── GenreService.java
    │   │   ├── LibraryService.java
    │   │   ├── SaleItemService.java
    │   │   ├── SaleService.java
    │   │   └── UserService.java          # CRUD users
    │   └── validator/                       # Domain validators
    │       ├── AuthorValidator.java
    │       ├── BookCopyValidator.java
    │       ├── DataValidator.java           # Central string/email/phone/ISBN validation
    │       ├── LibraryValidator.java
    │       ├── SaleItemValidator.java
    │       ├── SaleValidator.java
    │       └── UserValidator.java         # Registration validation
    └── test/java/hei/school/library/
        ├── conf/
        │   ├── FacadeIT.java                # Integration test base (TestContainers)
        │   ├── BucketConf.java              # S3 bucket test config
        │   └── EmailConf.java               # Email test config
        ├── controller/                      # Controller layer tests (@WebMvcTest + MockMvc)
        │   ├── arrival/ArrivalControllerTest.java
        │   ├── arrivalItem/ArrivalItemControllerTest.java
        │   ├── auth/AuthControllerTest.java
        │   ├── author/AuthorControllerTest.java
        │   ├── book/BookControllerTest.java
        │   ├── bookCopy/BookCopyControllerTest.java
        │   ├── customer/CustomerControllerTest.java
        │   ├── genre/GenreControllerTest.java
        │   ├── library/LibraryControllerTest.java
        │   └── user/UserControllerTest.java
        └── service/                         # Service layer tests (mocked repos, no Spring context)
            ├── arrivalItem/
            │   ├── DeleteArrivalItemServiceTest.java
            │   ├── GetArrivalItemServiceTest.java
            │   └── PostArrivalItemServiceTest.java
            ├── arrivals/
            │   ├── ArrivalFindByIdServiceTest.java
            │   ├── ArrivalFindByLibraryIdServiceTest.java
            │   └── PostArrivalServiceTest.java
            ├── auth/
            │   └── PostAuthServiceTest.java
            ├── authors/
            │   ├── AuthorServiceTest.java
            │   ├── DeleteAuthorsByIdServiceTest.java
            │   ├── GetAuthorsByIdServiceTest.java
            │   ├── GetAuthorsServiceTest.java
            │   └── PostAuthorsServiceTest.java
            ├── book/
            │   ├── DeleteBookByIdServiceTest.java
            │   ├── GetBookByIdServiceTest.java
            │   ├── GetBookServiceTest.java
            │   ├── PatchBookByIdTest.java
            │   └── PostBookServiceTest.java
            ├── bookCopy/
            │   ├── DeleteBookCopyServiceTest.java
            │   ├── GetBookCopyByIdServiceTest.java
            │   ├── GetBookCopyServiceTest.java
            │   ├── GetStockBookCopyServiceTest.java
            │   ├── PatchBookCopyServiceTest.java
            │   └── PostBookCopyServiceTest.java
            ├── customer/
            │   ├── DeleteCustomersByIdServiceTest.java
            │   ├── GetCustomersByIdServiceTest.java
            │   ├── GetCustomersServiceTest.java
            │   ├── PatchCustomersServiceTest.java
            │   └── PostCustomersServiceTest.java
            ├── genre/
            │   ├── GenreServiceTest.java
            ├── library/
            │   ├── LibraryServiceTest.java
            ├── sale/
            │   ├── GetSalesServiceTest.java
            │   ├── PatchSalesServiceTest.java
            │   └── PostSalesServiceTest.java
            ├── saleItem/
            │   ├── SaleItemServiceTest.java
            └── user/
                ├── DeleteUsersByIdServiceTest.java
                ├── GetUsersByIdServiceTest.java
                ├── GetUsersServiceTest.java
                └── PatchUsersServiceTest.java
```

## Common commands
```bash
# Build everything (excl. tests)
./gradlew build -x test

# Run tests
./gradlew test

# Run with coverage + report
./gradlew test jacocoTestReport

# Run full pipeline (test → coverage verification → report)
./gradlew build

# Run application locally (needs .env with DB creds)
./gradlew bootRun

# Format code
./format.sh
```

## Conventions
- **Package:** `hei.school.library`
- **POJA convention:** Code generated by the POJA scaffold is annotated `@PojaGenerated` — don't edit by hand.
- **Generated code exclusion:** `**/gen/**` is excluded from JaCoCo coverage.
- **Parallel tests:** `maxParallelForks = CPU/2` (set in `build.gradle`).
- **DB schema:** Hibernate `ddl-auto=validate` — schema is managed externally (Flyway-like via POJA pipeline), not by JPA auto-DDL.
- **OpenAPI:** Hand-authored spec at `doc/openapi.yml` (3.0.3, 27 paths, 58 ops). API base `/api/v1`. No auth required yet.
- **Lombok:** Used everywhere (builders, getters, setters, constructors).
- **AWS Lambda:** Deployed as a Lambda container (`springboot3` container type). `LambdaHandler` is the entry point. Deployment via Poja API.
- **Mail:** Uses AWS SES via Jakarta Mail. Configured in `EmailConf`.
- **TestContainers:** Integration tests use PostgreSQL module. `FacadeIT` is the base test class.
- **Architecture layers:** Controller ↔ Service ↔ Repository. Controllers only handle DTOs, services validate + orchestrate, repositories return entities.
- **Mapper:** Entity → DTO conversion in dedicated mapper classes under `mapper/`. Called in the service, not the repository. Builder pattern.
- **Validation:** Centralised in `DataValidator` (package `validator`) for shared rules (email, phone, ISBN, safe strings). Domain-specific validators (`AuthorValidator`, `BookCopyValidator`, etc.) handle per-domain rules. No JSR-380 annotations on DTOs.
- **Exceptions:** Standardised hierarchy — `NotFoundException` (404), `BadRequestException` (400), `UnprocessableEntityException` (422), `ConflictException` (409). All handled by `GlobalExceptionHandler` which returns JSON `ErrorBody`.
- **INSERT RETURNING pattern:** Repositories use native `@Query` with `INSERT ... RETURNING *` to insert and return the entity in one round-trip. Return type is `Optional<Entity>`, never a DTO.
- **Controller tests:** `@WebMvcTest` with `@MockBean` services and `MockMvc`. `GlobalExceptionHandler` is wired as an extra controller. Test classes under `controller/<domain>/`.
- **Service tests:** Live under `src/test/java/hei/school/library/service/<domain>/`. Mock repository — no Spring context, no `@ExtendWith(SpringExtension.class)`. Constructor injection only (no `@Autowired`).
- **Enum entities:** `BookCopyFormat` (HARDCOVER, PAPERBACK, POCKET), `BookCopyStatus` (AVAILABLE, SOLD_OUT), `SaleStatus` (SOLD, BOOKED, EXPIRED).
- **API base path:** `/api/v1` (defined in OpenAPI `servers`; controllers use `@RequestMapping` accordingly).

## Common pitfalls
- Don't commit `.env` (already in `.gitignore`).
- JaCoCo `minimum` is set to `0` — no hard coverage gate, but the report must still pass.
- Generated OpenAPI code won't appear in `src/` — it's in `build/generated/`.
- `spring.jpa.open-in-view=false` — no lazy loading in views; manage transactions explicitly.
- When adding new AWS resources, update both `build.gradle` (SDK deps) and `LambdaHandler` if the runtime role doesn't cover it.
- `format.sh` must pass (`./format.sh && git diff --exit-code`) in CI.

## CI/CD
Three GitHub Actions workflows:
- **ci.yml** — Runs `./gradlew test` and `./format.sh` on every push/PR to any branch.
- **cd-compute.yml** — Deploys to AWS Lambda on push to `preprod` or `prod`. Uses the Poja API to orchestrate deployment (builds via SAM, uploads to S3, triggers Poja deployment).
- **release-version.yml** — Version release workflow.

The repo goes through `preprod` → `prod` branches (remote `origin/preprod` exists).

---

*This file is for AI agents and human contributors. Keep it updated as the project evolves.*
