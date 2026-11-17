# CLAUDE.md

Architecture PoC for **schema-per-tenant multitenancy** with Spring Boot 4.1 (Java 25), Hibernate 7, Flyway and MySQL.

## Commands

```bash
./mvnw test                 # unit tests (TenantNamesTest); contextLoads needs a running MySQL
./mvnw spring-boot:run      # needs MySQL on localhost:3306, see application.properties
```

The `multitenancy_main` database must exist before startup. Everything else is created by the app.

## How it works

- **Main database** (`multitenancy_main`): holds the `tenants` table (`db/migration`). It is the control plane.
- **Tenant schemas** (`multitenancy_<tenantName>`): one MySQL schema per tenant, with scripts in `db/tenant/migration`.
- `RoutingDataSource`: one Hikari pool per tenant in a static `ConcurrentHashMap`; `main` is the default pool.
- `MyMultiTenantConnectionProvider` + `TenantIdentifierResolver`: Hibernate picks the pool by `TenantContext` (ThreadLocal).
- `TenantIdentifierInterceptor`: reads the `tenant` header for every endpoint except `/tenant/**`. Missing header returns 400, unknown tenant returns 404.
- `FlywayMigrationInitializer`: on startup migrates `main`, then every tenant in `tenants` (sequentially). `TenantService.createTenant` migrates the new tenant immediately.

## API

- `POST /tenant` with JSON `{"tenantName": "acme"}` creates the schema, the pool and runs the tenant migrations. Returns 400 for an invalid name and 409 for a duplicate.
- `GET /tenant` lists tenants.
- `GET /person` with header `tenant: <name>` reads from that tenant's schema.

## Conventions and rules

- **Tenant names are untrusted input.** They go into DDL and into the JDBC URL. Always go through `TenantNames` (whitelist `^[a-z][a-z0-9_]{2,30}$`, backtick-quoted identifiers). Never concatenate a name into SQL or a URL yourself.
- Add tenant schema changes as a new `db/tenant/migration/V<n>__*.sql`; main-database changes go in `db/migration`. Never edit an applied script.
- Always close JDBC `Connection` and `Statement` with try-with-resources. The pools are small.
- Control-plane endpoints must stay excluded from the tenant interceptor in `WebMvcConfig`.

## Intentional PoC decisions (do not "fix" without asking)

- No authentication or authorization; the `tenant` header is trusted.
- Hardcoded root DB credentials in `application.properties`.
- A tenant created on one instance is not known to other instances until they restart.
- Tenant migrations run only at startup, because every new version is deployed automatically.

## Known gaps (not addressed)

- A failed `createTenant` can leave an orphan schema and pool.
- One Hikari pool per tenant is never evicted, so it will not scale to many tenants.
- `baselineOnMigrate(true)` can skip `V1` on a non-empty schema.
- `V2__create_table_address.sql` (tenant) has no auto-increment, foreign key or index.
- `allow-circular-references=true` hides a cycle between the Flyway initializer, `TenantService` and the entity manager factory.
- No integration tests (for example Testcontainers tests for tenant isolation).
