---
name: implement-use-case
description: Implement one use case from docs/requirements.md given its id (e.g. UC-006). Use when asked to implement a use case or requirement by id.
---

# Implement use case

Input: a use case id, e.g. `UC-006`.

1. Read the use case and its dependencies in `docs/requirements.md`. Stop if a dependency is not Completed.
2. Read relevant existing code to follow established patterns (load the `spring-boot` skill).
3. If anything is ambiguous (fields, rules, edge cases, who can do what), ASK the user concise questions
   with a suggested default for each. Do not guess.
4. Implement: Flyway migration (if needed) -> domain -> `*Api`/models (if other modules need it) -> web -> templates.
5. Write tests that verify each acceptance criterion.
6. Run `mvn spotless:apply` then `mvn verify`; fix until green (architecture tests are non-negotiable).
7. Set the use case Status to `Completed` in `docs/requirements.md`.
8. If the user answered clarification questions, run the `update-use-case` skill to sync the spec.
9. Summarize: files changed, decisions taken, anything left open.
