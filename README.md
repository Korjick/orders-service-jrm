# Orders Service

A reactive RESTful microservice for creating customer orders and retrieving user order history.

Part of: https://github.com/topics/korjick-java-microservice-restaurant

## Tech Stack

- **Language & Framework:** Java 25, Spring Boot 4 (Spring WebFlux, Project Reactor)
- **Persistence:** Spring Data R2DBC, PostgreSQL R2DBC Driver, Custom Converters (JSONB)
- **Database & Migrations:** PostgreSQL, Flyway
- **HTTP Client:** Spring WebClient (with Retry & Backoff)
- **Mapping & Utilities:** Lombok, Jackson
- **Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Testing:** JUnit 5, Testcontainers, WireMock, MockWebServer, Reactor Test, AssertJ, WebTestClient
- **Containerization:** Cloud Native Buildpacks

## API Endpoints

- `POST /v1/menu-orders` - Submit and create a new customer order (requires `X-User-Name` header).
- `GET /v1/menu-orders?from={from}&size={size}&sortBy={sortBy}` - Retrieve a paginated list of user orders sorted by creation date (`date_asc`, `date_desc`, requires `X-User-Name` header).

## Build and Run

```bash
# Run tests
./gradlew test

# Run application
./gradlew bootRun

# Build and run containerized application
./gradlew bootBuildImage
cd ./docker
docker compose up -d
```
