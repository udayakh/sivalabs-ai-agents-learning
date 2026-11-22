# Day 7: Harness engineering

## Goal
Turn conventions into **deterministic, build-breaking checks** so agents (and humans) can't silently violate them.

## Key ideas
- Agents are **task-oriented**: they do whatever gets the task done, sometimes producing slop.
- Tools like Claude Code/Codex/Air are already a harness (loops, retries, limits). **Your project harness** adds:
  - **Formatting**: Spotless + Palantir (`spotless:apply` / `spotless:check`). No time wasted on tabs vs spaces.
  - **Coverage gate**: JaCoCo `check`. Coverage ≠ correctness, but a floor helps. Write *useful* tests.
  - **Module boundaries**: Spring Modulith `modules.verify()`.
  - **Architecture rules**: ArchUnit (custom rules) and **Taikai** (ready-made Java/Spring rules on top of ArchUnit).
  - Optional: SonarQube (+ MCP server so the agent can list and fix issues), OWASP dependency check, Checkstyle.
- A failing test is non-negotiable feedback the agent must fix, unlike a markdown guideline.
- Your time goes to what the harness can't judge: **business logic and test quality** (AI writes many superficial tests).
- Be pragmatic: exclusions are fine when documented (we exclude `WebSecurityConfig` from Taikai's "no generic
  exceptions" rule because `HttpSecurity#build()` declares `throws Exception`).

## In this repo
- `pom.xml`: Spotless + JaCoCo (`jacoco.minimum.coverage=0.70`; actual ~98%).
- `architecture/ArchitectureTest.java`: entities/repositories/controllers not public; **custom condition**:
  `@Service` classes only used inside their own module; domain must not depend on web/servlet API.
- `architecture/TaikaiArchitectureTest.java`: no `@Autowired` fields, naming, package-private controllers/repositories, etc.

## Hands-on
1. Make `ProductEntity` `public` → `mvn test -Dtest=ArchitectureTest` → read message → revert.
2. Add `@Autowired` on a field somewhere → run Taikai test → revert.
3. Mess up indentation in a file → `mvn spotless:check` fails → `mvn spotless:apply`.
4. Raise `jacoco.minimum.coverage` to `0.99` → `mvn verify` fails → revert.
5. **Write a new rule**: "classes in `..web..` must not depend on classes annotated with `@Entity`". Make it pass.
6. Read `ProductReviewsTests`: which tests verify business rules, which only check status codes?

## Real lesson from this repo: a harness that silently doesn't run
`ArchitectureTest` was written but **never executed**: `@ArchTest` fields need the ArchUnit JUnit 5 *engine*
(`com.tngtech.archunit:archunit-junit5`), and Taikai only brings the API. Maven reported 0 tests and stayed green.
Found it during exercise 1 (making an entity public did not fail the build). Fix: add the engine dependency → 56 tests.
**Takeaway:** test the harness itself — break a rule on purpose and confirm the build goes red.

## Interview questions
- *What is harness engineering?* → Deterministic guardrails (formatters, tests, architecture rules, coverage gates,
  static analysis) around AI agents so output quality doesn't depend on the agent following instructions.
- *Is high coverage enough?* → No. 100% coverage can still miss logic bugs; review whether tests assert business rules.
- *ArchUnit vs Spring Modulith?* → Modulith: module boundaries/cycles. ArchUnit/Taikai: finer rules (visibility, naming, layering).
