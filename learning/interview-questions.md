# Interview question bank (with model answers)

Each answer points to code in `review-buddy` so you can back up claims with real examples.

## AI-assisted development

**1. How do you use AI coding agents?**
As an amplifier. Brainstorm and write a minimal PRD with AI, scaffold with deterministic tools, encode conventions
as agent skills, enforce them with a harness (tests/rules/gates), build one spec at a time, and review business logic
and tests myself. Example: Review Buddy (`README.md`).

**2. What is harness engineering?**
Deterministic guardrails so quality doesn't depend on an LLM following instructions: Spotless formatting, JaCoCo
coverage gate, Spring Modulith boundary verification, ArchUnit/Taikai architecture rules, optionally SonarQube/OWASP.
A violation fails the build, which the agent must fix. (`architecture/*Test.java`, `pom.xml`)

**3. Skills vs AGENTS.md?**
AGENTS.md is sent on every request: keep it short (commands, non-negotiables). Skills are loaded on demand based on
their description: detailed guidelines or tools without wasting tokens. (`AGENTS.md`, `.agents/skills`)

**4. Do you still review AI-generated code?**
For long-lived systems, yes: agents over-engineer and occasionally ignore rules; harness catches structure, not
business correctness or useless tests. For throwaway tools, not necessarily. Accountability stays with me.

**5. Spec-driven development or vibe coding?**
Depends on risk and certainty. Business features with clear rules → spec with acceptance criteria. Exploratory
work → vibe code, then ask AI to write the spec of what was built. Always one spec at a time.

**6. How do agents learn about new library versions?**
Context7 MCP (version-aware docs), llms.txt links in AGENTS.md, official docs MCP servers (Vaadin, jOOQ, Angular).

**7. Software factory: would you use it?**
Not yet for real systems. Upfront requirements are incomplete; one wrong assumption compounds; the team loses touch
with design decisions. I keep humans in the loop per spec.

## Spring / architecture

**8. Explain your module structure.**
Modules `users`, `products`, `reviews` + open `shared` + `config`. Public surface = root `*Api` classes and `models`
named interfaces; entities, repositories, controllers package-private. Dependencies: reviews → products → users, acyclic.

**9. How did you avoid a cycle between products and reviews?**
The details page (shows reviews) lives in `reviews.web`. Products only exposes `ProductApi.updateRatingStats`;
rating stats are denormalized into the products table so sorting by rating needs no join to reviews.

**10. Synchronous vs event-based module communication?**
Sync API calls when I need an answer in the same transaction (rating stats). Events for side effects that can be
async (AI summary) via `@ApplicationModuleListener` (async, new transaction, after commit).

**11. What if the app crashes before the summary is generated?**
The Modulith event publication registry persisted the event in the same transaction as the review; it stays
incomplete and is republished on restart (`spring.modulith.events.republish-outstanding-events-on-restart=true`).

**12. JPA best practices you applied?**
`open-in-view=false`, `ddl-auto=validate` + Flyway, fetch joins with explicit count queries for pagination,
no cross-module associations (ids only), `@Version`, auditing via `BaseEntity`, DTO projections to the view,
empty-string filters to avoid PostgreSQL null-type inference issues, `in_clause_parameter_padding`.

**13. Security design?**
Form login with DB users (`SecurityUserDetailsService`), BCrypt via DelegatingPasswordEncoder, CSRF on forms, GET
browsing public, writes authenticated. Business authorization in the service (no self-review, one review per user)
plus a DB unique constraint as the final safety net.

## Spring AI

**14. How is the LLM integrated?**
`ReviewSummarizer` interface; `SpringAiReviewSummarizer` uses `ChatClient` with a system prompt + user message;
`AiSummaryConfig` chooses it only if a ChatClient exists (profile `ai`), otherwise `SimpleReviewSummarizer`.

**15. What if OpenAI is slow or down?**
User isn't affected (async after commit). Spring AI retries twice; then we catch the error and use the rule-based
fallback. The UI labels who generated the summary.

**16. How do you test it?**
`ChatClient.create(fakeChatModel)`: the real Spring AI pipeline, fake model. Assert prompt content, happy path,
exception fallback, blank fallback. Integration test awaits the async summary with Awaitility.

**17. Prompt injection?**
Reviews are untrusted: system prompt instructs to treat them as data; summaries are labelled as AI-generated;
user text is not run through a template engine. For higher risk: output validation, moderation, structured output.

**18. How would you improve it?**
Structured output (`.entity(Summary.class)`), don't hold a DB transaction during the LLM call (load → call → save in
short transactions), cache/debounce regeneration, Testcontainers PostgreSQL, observability of token usage/cost.
