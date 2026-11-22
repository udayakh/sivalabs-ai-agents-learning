---
name: update-use-case
description: Sync a use case in docs/requirements.md with decisions taken during implementation or with the current behaviour of the code. Use after clarifications, or to document a feature that was vibe coded.
---

# Update use case

1. Identify the use case id (ask if unclear). If none exists (feature was vibe coded), create a new id.
2. Collect decisions from the conversation and read the current implementation and tests.
3. Update the use case: description, a "Clarifications captured during implementation" list, acceptance criteria.
   Business language only; mention config keys if behaviour is configurable.
4. Do not change code. Show the diff of the spec to the user.
