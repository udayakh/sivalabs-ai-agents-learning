---
name: prd-to-requirements
description: Turn docs/prd.md (and optional UI mockups) into docs/requirements.md with numbered use case specifications. Use when asked to derive requirements or use cases from a PRD.
---

# PRD to requirements

1. Read `docs/prd.md`. If a `mockup/` directory exists, read it to derive fields and screens.
2. Check what is already implemented (e.g. auth) and mark those use cases `Completed`.
3. Write `docs/requirements.md`:
   - Intro line explaining the document.
   - Summary table: `ID | Use case | Status | Depends on` (IDs `UC-001`, `UC-002`, ...).
   - One section per use case: Status, Depends on, Description (business language, no implementation details),
     Acceptance criteria (testable bullet points).
4. Keep each use case small enough to implement in one agent session.
5. List open questions at the end instead of guessing. Do not invent features outside the PRD's MVP.
