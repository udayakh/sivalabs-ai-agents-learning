# Review Buddy

A tiny Product Hunt style app: makers submit products, users rate and review them
("what I like" / "what to improve"), and **Spring AI** summarizes what people are saying.
It was built following the *Building with AI Agents - From Idea to Production* course workflow:
idea → PRD → scaffold → agent skills → harness engineering → spec-driven use cases → latest tech with AI.

| | |
|---|---|
| Stack | Java 21, Spring Boot 4.1, Spring Modulith 2.1, Spring AI 2.0 (OpenAI), Spring Security 7, Spring Data JPA / Hibernate 7, Flyway, Thymeleaf + Tailwind |
| DB | H2 (PostgreSQL mode, default, zero setup) or PostgreSQL via Docker Compose |
| Harness | Spotless (Palantir format), JaCoCo ≥ 70% gate, Spring Modulith `verify()`, ArchUnit, Taikai |
| Tests | 49 tests, ~98% line coverage, no network/API key needed |

## Run it

```bash
mvn spring-boot:run                                        # http://localhost:8080 (H2, data in ./data)
mvn spring-boot:run -Dspring-boot.run.profiles=postgres    # PostgreSQL from compose.yaml (Docker running)
# real LLM summaries (PowerShell: $env:OPENAI_API_KEY="sk-...")
OPENAI_API_KEY=sk-... mvn spring-boot:run -Dspring-boot.run.profiles=ai

# FREE local LLM summaries via Ollama (install Ollama, then: ollama pull llama3.2:3b)
mvn spring-boot:run -Dspring-boot.run.profiles=ollama

# PostgreSQL (Docker) + local LLM; profiles combine
mvn spring-boot:run -Dspring-boot.run.profiles=postgres,ollama
mvn verify                                                 # all tests + quality gates
```

Demo users: `user@gmail.com / user`, `admin@gmail.com / admin`, `alice@gmail.com / secret`, `bob@gmail.com / secret`.

**Try the AI flow:** log in as Bob → open *Progen* (it has 2 reviews) → write the 3rd review →
reload: the "What people are saying" box now shows a summary generated asynchronously.

## Architecture

```
dev.udaylabs.reviewbuddy
├── config/     WebSecurityConfig, PersistenceConfig (JPA auditing), AsyncConfig, GlobalExceptionHandler
├── shared/     OPEN module: BaseEntity, PagedResult, Slugs, exceptions
├── users/      UserApi ─ models/UserDto ─ domain/(UserEntity, UserService, SecurityUserDetailsService) ─ web/AuthController
├── products/   ProductApi ─ models/(ProductDto, CategoryDto, ProductSearchCriteria, ...) ─ domain/ ─ web/ProductController
└── reviews/    models/(ReviewDto, ReviewSubmittedEvent, ...) ─ domain/ReviewService ─ web/ProductDetailsController
                └── ai/  ReviewSummarizer ← SpringAiReviewSummarizer | SimpleReviewSummarizer (fallback)
                         ReviewSummaryEventListener (@ApplicationModuleListener), ReviewSummaryService
```

Module dependencies (generated in `target/spring-modulith-docs/components.puml`):
`reviews → products → users`, `reviews → users`, everything → `shared`. **No cycles.**

### Key design decisions (good interview material)

1. **Modular monolith (Spring Modulith).** Each top-level package is a module. Other modules may only use
   a module's `*Api` class and its `models` named interface. Entities/repositories/controllers are
   package-private. Verified by `ModularityTest` on every build.
2. **Narrow public API.** `ProductApi` exposes 3 methods even though `ProductService` has more.
3. **Avoiding a dependency cycle.** The product details page shows reviews, so it lives in
   `reviews.web.ProductDetailsController`; products never depends on reviews. Rating stats are
   denormalized into `products` (`average_rating`, `review_count`) via `ProductApi.updateRatingStats`,
   so sorting by "top rated" is a cheap indexed query.
4. **Event driven AI summary.** `ReviewService` publishes `ReviewSubmittedEvent`. The listener uses
   `@ApplicationModuleListener` = async + new transaction + after commit. The user never waits for the LLM,
   and the Modulith **event publication registry** (`event_publication` table) records incomplete events,
   which are republished on restart (transactional outbox pattern).
5. **Graceful degradation.** `AiSummaryConfig` picks `SpringAiReviewSummarizer` only when a `ChatClient`
   is available (profile `ai`), otherwise the deterministic `SimpleReviewSummarizer`. LLM errors or empty
   answers also fall back. Spring AI retries (`spring.ai.retry.max-attempts=3` = retry twice).
6. **Prompt-injection awareness.** Reviews are untrusted input; the system prompt tells the model to treat
   them as data. The user text is sent as a `Prompt` object (not a template), so `{braces}` in reviews are safe.
7. **Testable AI.** `SpringAiReviewSummarizerTest` runs the real `ChatClient` against a fake `ChatModel`:
   no network, no key, deterministic.
8. **JPA good practices.** `open-in-view=false`, `ddl-auto=validate` + Flyway, `join fetch` + explicit
   `countQuery` for pagination, empty-string filters instead of nulls (PostgreSQL can't infer null parameter types),
   no cross-module associations (ids only), `@Version` optimistic locking, auditing via `BaseEntity`.
9. **Security.** Form login, BCrypt via `DelegatingPasswordEncoder` (`{bcrypt}` prefix), CSRF on all forms,
   public GET for browsing, authentication for writes. Business rules: one review per user per product,
   makers can't review their own products (enforced in the service, shown in the UI, backed by a DB unique constraint).

## Harness engineering: what fails the build

| Check | Tool | Where |
|---|---|---|
| Formatting | Spotless + Palantir Java Format | `pom.xml` (`spotless:check` in `verify`) |
| Coverage ≥ 70% lines | JaCoCo | `pom.xml` |
| Module boundaries, no cycles | Spring Modulith | `architecture/ModularityTest` |
| Entities/repositories/controllers not public, services only used inside their module, domain ≠ web | ArchUnit | `architecture/ArchitectureTest` |
| No `@Autowired` fields, naming, no deprecated APIs, no generic exceptions, no System.out, … | Taikai | `architecture/TaikaiArchitectureTest` |

Try it: make `ProductEntity` public and run `mvn test -Dtest=ArchitectureTest` and watch it fail.

## AI agent setup in this repo

- `AGENTS.md` (+ `CLAUDE.md` importing it): short, only commands, non-negotiables, workflow, doc sources.
- `.agents/skills/`: `spring-boot` (with references), `spring-modulith-verifier`, `prd-to-requirements`,
  `implement-use-case`, `update-use-case`.
- `.mcp.json.example`: Context7 (up-to-date library docs) and Playwright (agent can look at pages).
  Copy it to `.mcp.json` to enable.
- `docs/prd-prompt.md`, `docs/prd.md`, `docs/requirements.md`: the spec-driven artifacts.

## How this project was built (mapped to the course)

| Course episode | What was done here |
|---|---|
| Brainstorm + PRD | `docs/prd-prompt.md` (skeptical co-founder prompt), minimal `docs/prd.md` with fixed sections |
| Scaffolding (Progen) | Versions picked via Spring Initializr metadata; Progen-style extras added by hand: Spotless, JaCoCo, Taskfile, `.sdkmanrc`, GitHub Actions, Renovate, compose.yaml, JPA properties, BaseEntity, PagedResult, error pages, security, users + login/registration |
| IDE/ADE | Works with IntelliJ + JetBrains Air, Claude Code, Codex. Agent instructions in `AGENTS.md` |
| Vibe coding mock-up | Tailwind CDN green/white theme (mock-up dir is git-ignored, as in the course) |
| Agent skills | `.agents/skills/*` |
| Harness engineering | Spotless, JaCoCo, Modulith, ArchUnit, Taikai tests |
| Spec-driven development | `docs/requirements.md` use cases + `implement-use-case` / `update-use-case` skills |
| Latest tech (Spring AI) | Context7/llms.txt guidance in `AGENTS.md`; Spring AI 2.0 `ChatClient` summaries |

## Not done / next steps
- Testcontainers PostgreSQL for tests (the machine used had Docker stopped, so tests use H2 PostgreSQL mode).
- UC-006 and UC-007 are left as practice exercises (see `learning/day-08`).
- Deployment (e.g. Fly.io / Railway).
