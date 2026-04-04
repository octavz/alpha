# Alpha Backend Rewrite Plan: Kotlin Spring Boot → Scala 3 ZIO

## Overview
Rewrite the existing Spring Boot Kotlin backend to Scala 3 with ZIO ecosystem.

## Technology Stack
- **Language**: Scala 3
- **Framework**: ZIO Http (zio-http)
- **Database**: PostgreSQL with zio-postgres (no JDBC)
- **JSON**: zio-json
- **Migration**: Flyway (keep existing migrations)
- **Testing**: zio-test + testcontainers
- **Coverage**: SCoverage (100% coverage target)
- **Build Tool**: sbt

## Architecture (Onion/Hexagonal)

```
backend-scala-zio/
├── src/main/scala/com/alpha/
│   ├── Main.scala                          # Entry point
│   ├── config/                             # Configuration layers
│   │   ├── AppConfig.scala
│   │   ├── DatabaseConfig.scala            # zio-postgres pool
│   │   ├── FlywayConfig.scala
│   │   └── JwtSettings.scala
│   ├── domain/
│   │   ├── model/Models.scala              # Domain entities
│   │   └── enums/Enums.scala               # Value objects
│   ├── repository/                         # Data access layer (SQL)
│   │   ├── UserRepository.scala
│   │   ├── SessionRepository.scala
│   │   ├── CategoryRepository.scala
│   │   ├── BusinessRepository.scala
│   │   ├── RegionRepository.scala
│   │   └── AppointmentRepository.scala
│   ├── service/                            # Business logic layer
│   │   ├── AuthService.scala
│   │   ├── CategoryService.scala
│   │   ├── BusinessService.scala
│   │   ├── RegionService.scala
│   │   └── AppointmentService.scala
│   ├── controller/                         # HTTP handlers
│   │   ├── AuthController.scala
│   │   ├── CategoryController.scala
│   │   ├── BusinessController.scala
│   │   ├── RegionController.scala
│   │   ├── AppointmentController.scala
│   │   └── HealthController.scala
│   └── security/
│       └── JwtService.scala                # JWT token handling
├── src/test/scala/com/alpha/
│   ├── domain/DomainSpec.scala             # Domain model tests
│   ├── config/ConfigSpec.scala             # Config tests
│   ├── security/JwtServiceSpec.scala       # JWT tests
│   ├── service/
│   │   ├── AuthServiceSpec.scala           # Auth service tests
│   │   └── CategoryServiceSpec.scala      # Category service tests
│   ├── repository/
│   │   ├── UserRepositorySpec.scala        # Repository tests (testcontainers)
│   │   └── CategoryRepositorySpec.scala    # Repository tests (testcontainers)
│   └── testutil/
│       └── PostgresContainer.scala         # Testcontainer helper
```

## Implementation Status

### Phase 1: Project Setup ✅
- [x] Create `build.sbt` with all dependencies
- [x] Add SCoverage plugin for code coverage
- [x] Add testcontainers for PostgreSQL testing
- [x] Add zio-json for JSON serialization
- [x] Create `application.yml`
- [x] Set up logging and error handling

### Phase 2: Domain Models ✅
- [x] Define domain models (User, Business, Category, etc.)
- [x] Create enum types (UserRole, VerificationStatus, AppointmentStatus)
- [x] DomainSpec tests for 100% coverage

### Phase 3: Database Layer ✅
- [x] Configure zio-postgres connection pool
- [x] Create repository layer with SQL queries (no JDBC)
- [x] Flyway migrations configured
- [x] UserRepositorySpec with testcontainers
- [x] CategoryRepositorySpec with testcontainers

### Phase 4: Security ✅
- [x] JWT token generation/verification
- [x] Password hashing
- [x] JwtServiceSpec tests

### Phase 5: Services ✅
- [x] AuthService (login, register, refresh, logout)
- [x] AuthServiceSpec tests with mocked repositories
- [x] CategoryService (CRUD operations)
- [x] CategoryServiceSpec tests
- [x] BusinessService (CRUD, search, featured)
- [x] RegionService (CRUD operations)
- [x] AppointmentService (CRUD, status management)

### Phase 6: HTTP Controllers ✅
- [x] Define routes with zio-http
- [x] Implement auth handlers (zio-json)
- [x] Implement category, business, region, appointment handlers
- [x] Health check endpoint

### Phase 7: Integration ✅
- [x] Wire up all components via ZIO layers
- [x] Flyway migration on startup
- [x] Server starts on port 3000

### Phase 8: Testing ✅
- [x] DomainSpec (domain model tests)
- [x] ConfigSpec (configuration tests)
- [x] JwtServiceSpec (JWT tests)
- [x] AuthServiceSpec (auth service tests with mocks)
- [x] CategoryServiceSpec (category service tests with mocks)
- [x] UserRepositorySpec (repository tests with testcontainers)
- [x] CategoryRepositorySpec (repository tests with testcontainers)
- [x] SCoverage plugin configured for 100% coverage

## Dependencies (build.sbt)
```scala
// Core ZIO
"dev.zio" %% "zio" % "2.1.0",
"dev.zio" %% "zio-streams" % "2.1.0",
"dev.zio" %% "zio-prelude" % "2.1.0",

// ZIO HTTP
"dev.zio" %% "zio-http" % "3.0.0",

// ZIO Postgres (no JDBC)
"dev.zio" %% "zio-postgres" % "0.4.0",

// ZIO JSON
"dev.zio" %% "zio-json" % "0.1.0",

// JWT
"github.jwt-scala" %% "jwt-core" % "6.2.0",

// PostgreSQL
"org.postgresql" % "postgresql" % "42.7.0",

// Flyway
"org.flywaydb" % "flyway-core" % "10.17.0",

// Testing
"dev.zio" %% "zio-test" % "2.1.0" % Test,
"dev.zio" %% "zio-test-container" % "1.0.0" % Test,
"com.dimafeng" %% "testcontainers-scala-postgresql" % "0.41.0" % Test,

// Coverage
"sbt-scoverage" % "2.2.1"
```

## Running Tests

```bash
# Run all tests
sbt test

# Run tests with coverage
sbt coverage test

# Generate coverage report
sbt coverageReport
```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/v1/health | Health check |
| POST | /api/v1/auth/login | User login |
| POST | /api/v1/auth/register | User registration |
| POST | /api/v1/auth/refresh | Refresh tokens |
| POST | /api/v1/auth/logout | User logout |
| GET | /api/v1/categories | List all categories |
| GET | /api/v1/categories/:id | Get category by ID |
| POST | /api/v1/categories | Create category |
| PUT | /api/v1/categories/:id | Update category |
| DELETE | /api/v1/categories/:id | Delete category |
| GET | /api/v1/businesses | List all businesses |
| GET | /api/v1/businesses/featured | List featured businesses |
| GET | /api/v1/businesses/search | Search businesses |
| GET | /api/v1/businesses/:id | Get business by ID |
| POST | /api/v1/businesses | Create business |
| PUT | /api/v1/businesses/:id | Update business |
| DELETE | /api/v1/businesses/:id | Delete business |
| PUT | /api/v1/businesses/:id/featured | Mark as featured |
| GET | /api/v1/regions | List all regions |
| GET | /api/v1/regions/:id | Get region by ID |
| POST | /api/v1/regions | Create region |
| PUT | /api/v1/regions/:id | Update region |
| DELETE | /api/v1/regions/:id | Delete region |
| GET | /api/v1/appointments/:id | Get appointment by ID |
| GET | /api/v1/appointments/business/:businessId | Get by business |
| GET | /api/v1/appointments/user/:userId | Get by user |
| POST | /api/v1/appointments | Create appointment |
| PUT | /api/v1/appointments/:id | Update appointment |
| PUT | /api/v1/appointments/:id/cancel | Cancel appointment |
| PUT | /api/v1/appointments/:id/confirm | Confirm appointment |
| PUT | /api/v1/appointments/:id/complete | Complete appointment |

## Port Configuration
- Backend: 3000
- Database: 5433 (PostgreSQL)
- Frontend: 5173

## Running the Project
```bash
cd backend-scala-zio
sbt run
```

## Environment Variables
```
PORT=3000
DATABASE_URL=postgresql://alpha_user:alpha_password@localhost:5433/alpha
JWT_SECRET=your_jwt_secret_here
JWT_ACCESS_SECRET=your_jwt_access_secret_here
JWT_REFRESH_SECRET=your_jwt_refresh_secret_here
JWT_ACCESS_EXPIRY=15m
JWT_REFRESH_EXPIRY=7d
CORS_ORIGIN=http://localhost:5173,http://localhost:5179
```
