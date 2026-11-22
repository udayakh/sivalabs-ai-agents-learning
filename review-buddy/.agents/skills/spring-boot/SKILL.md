---
name: spring-boot
description: Build Spring Boot 4.x applications following our best practices. Use this skill when creating or changing controllers, services, repositories, JPA entities, DTOs, Flyway migrations, Thymeleaf views, package structure, or tests in a Spring Boot application.
---

# Spring Boot guidelines

Load only the reference you need:
- Package structure / module rules -> [references/code-organization.md](references/code-organization.md)
- Writing tests -> [references/testing.md](references/testing.md)

## Core rules
- Package by **feature/module**, never by layer (no global `controllers`, `services` packages).
- Constructor injection only, no `@Autowired` fields. Keep classes package-private unless another package needs them.
- Controllers: `@Controller` (Thymeleaf) or `@RestController`, package-private, thin; validate input with
  `@Valid` form records + `BindingResult`; never call repositories directly.
- Services: `@Service`, `@Transactional(readOnly = true)` at class level, `@Transactional` on write methods.
  Accept command records, return DTO records - never entities.
- Entities: package-private, extend `shared.BaseEntity` (auditing + `@Version`), `IDENTITY` ids,
  protected no-arg constructor, no setters - expose intention revealing methods (`updateRatingStats(...)`).
- Repositories: package-private `JpaRepository`; use `join fetch` for associations needed by the view;
  always provide `countQuery` for paginated fetch-join queries.
- Schema: Flyway `V<n>__description.sql`; portable SQL (works on H2 PostgreSQL mode and PostgreSQL);
  use `varchar(n)` not `text`. `spring.jpa.hibernate.ddl-auto=validate`. Never modify applied migrations.
- `spring.jpa.open-in-view=false`.
- Views: Thymeleaf + layout fragments in `templates/fragments/layout.html`, Tailwind via CDN, green/white theme.
- Finish every task with `mvn spotless:apply` and `mvn verify`.
