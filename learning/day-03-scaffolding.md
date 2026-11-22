# Day 3: Scaffolding a production-grade Spring Boot app

## Goal
Understand what a good starting point contains and why we don't spend AI tokens on it.

## Key ideas
- **Use deterministic tools** (Spring Initializr, Progen CLI) to scaffold, not the AI. It's free, repeatable and
  uses current versions (AI may generate an old Spring Boot 3.x project).
- Initializr gives dependencies only. Progen adds what you configure in *every* project:
  Spotless, JaCoCo, Taskfile, `.sdkmanrc`, GitHub Actions, Renovate, compose.yaml, JPA best-practice properties,
  virtual threads, actuator exposure, error pages, security config, BaseEntity with auditing, PagedResult,
  login/registration, local Flyway clean.
- You can't ask AI for tools you don't know exist (ArchUnit, Taikai…), so ecosystem knowledge still matters.
- Java version: pick the newest one that's available in SDKMAN **and** your CI (GitHub Actions).

## In this repo (read in this order)
1. `review-buddy/pom.xml`: Boot 4.1 parent, Spring AI / Modulith BOMs, Spotless + JaCoCo plugins.
2. `src/main/resources/application.properties`: note `open-in-view=false`, `ddl-auto=validate`, virtual threads,
   Modulith event registry, Spring AI disabled by default.
3. Profiles: `application-postgres.properties` (Docker Compose integration), `application-ai.properties`.
4. `db/migration/V1..V3`: Flyway, portable SQL, seeds, BCrypt `{bcrypt}` passwords.
5. `config/WebSecurityConfig.java`, `shared/BaseEntity.java`, `shared/PagedResult.java`.
6. `Taskfile.yml`, `.sdkmanrc`, `.github/workflows/maven.yml`, `renovate.json`, `compose.yaml`.

## Hands-on
1. Generate a project on https://start.spring.io with web, thymeleaf, security, data-jpa, flyway, modulith,
   spring-ai-openai. Diff it with `review-buddy`: list 10 things we added.
2. Optional: install Progen (`go install` or a release binary) and generate a Thymeleaf app. Compare.
3. Explain why the SQL uses `varchar(2000)` instead of `text` (hint: H2 maps text to CLOB → Hibernate validate fails).

## Interview questions
- *Why `spring.jpa.open-in-view=false`?* → Avoids lazy loading in the view layer holding a DB connection for the
  whole request and hidden N+1 queries; forces services to return complete DTOs.
- *Why `ddl-auto=validate` + Flyway?* → Schema is versioned and reviewed; Hibernate only checks the mapping matches.
- *Never edit an applied migration. Why?* → Flyway checksums fail and other environments diverge; add a new version.
