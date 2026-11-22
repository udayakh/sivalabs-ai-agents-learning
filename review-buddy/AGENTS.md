# AGENTS.md - instructions for AI coding agents

Review Buddy: Spring Boot 4.1 + Spring Modulith 2.1 + Spring AI 2.0 + Thymeleaf + Spring Data JPA.
Java 21, Maven. Keep this file short: detailed guidance lives in agent skills (`.agents/skills`),
which agents load on demand (saves tokens compared to one huge AGENTS.md).

## Commands
- Build + all checks: `mvn verify` (tests, ArchUnit/Taikai/Modulith rules, JaCoCo >= 70%, Spotless check)
- Format code: `mvn spotless:apply` (always run before finishing a task)
- Run app: `mvn spring-boot:run` (H2), `-Dspring-boot.run.profiles=postgres` (Docker), `ai` (needs OPENAI_API_KEY), `ollama` (free local LLM)

## Non-negotiables (enforced by tests - a failing build means the task is not done)
- Modules = top level packages under `dev.udaylabs.reviewbuddy` (`users`, `products`, `reviews`, `shared`, `config`).
- Other modules may only use a module's root package (`*Api` classes) and its `models` named interface.
- Entities, repositories and controllers are package-private. Never expose entities; return DTO records.
- No cross-module JPA associations: reference other modules by id.
- Constructor injection only. Flyway for schema changes - never edit an applied migration, add a new `V<n>__*.sql`.

## Workflow
- Requirements live in `docs/requirements.md`. Use the `implement-use-case` skill with a use case id.
- Ask clarification questions before implementing ambiguous requirements; then update the spec (`update-use-case`).

## Up-to-date library docs (models have a knowledge cutoff)
- Use the Context7 MCP server for Spring Boot / Spring AI / Spring Modulith APIs matching the versions in `pom.xml`.
- llms.txt references:
  - Spring AI reference: https://docs.spring.io/spring-ai/reference/
  - Tailwind CSS: https://tailwindcss.com/docs
- Use the Playwright MCP server to look at rendered pages when changing templates.
