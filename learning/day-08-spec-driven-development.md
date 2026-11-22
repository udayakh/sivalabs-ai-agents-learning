# Day 8: Spec-driven development (SDD)

## Goal
Implement a feature end-to-end with an agent, one spec at a time.

## Key ideas
- Two schools:
  1. **Spec as a detailed prompt in a file**: implement, verify, archive/throw away.
  2. **Spec as a living, first-class artifact** synced with the code (OpenSpec, Spec Kit, BMAD, AI Unified Process).
- Classic flow: BA writes use case → dev clarifies → implementation plan → agent implements → another model reviews (review.md).
  With strong models (Opus, GPT) + harness, the **plan step often becomes unnecessary**: detailed spec → implement.
- **Is SDD waterfall?** Not when the cycle is minutes, not months. But for **exploratory** work (libraries, frameworks)
  you can't specify up front → **hybrid**: vibe code until it works, then ask AI to write the spec of the current behaviour.
- **One spec at a time.** Planning every use case up front breaks as soon as you discover missing work (layout, test data…).
- The agent should **ask clarification questions**; then update the spec so it matches the code (`update-use-case`).
- Keep the process simple: "the simpler the process, the better the chance you succeed".

## In this repo
- `docs/requirements.md`: UC-000…UC-005 Completed, **UC-006 and UC-007 open** for you.
- UC-005 shows a "Clarifications captured during implementation" list (min 3 reviews, async, retry twice, fallback).
- Skills: `prd-to-requirements`, `implement-use-case`, `update-use-case`.

## Hands-on: implement UC-006 "My products and my reviews" with an agent
1. Refine the spec first: let the agent interview you (`grill-me` style). Decide: URL `/me`? pagination? show summaries?
2. Run: *"Use the implement-use-case skill for UC-006."* (Claude Code: `/implement-use-case UC-006`).
3. Expected design (check the agent's work against this):
   - `ProductApi.findBySubmitter(userId)` + `ProductService` + repository query.
   - Reviews by user in `ReviewService`; where should the `/me` controller live without creating a cycle? (hint: `reviews` already depends on products and users.)
   - Template `templates/me.html` following your Day 4 mock-up; security: authenticated only.
   - Tests: anonymous → login redirect; user sees only *their* products/reviews.
4. `mvn verify` must be green. Then run `update-use-case` for UC-006.
5. Stretch: UC-007 (edit/delete review). Remember to recompute rating stats and republish the event.

## Interview questions
- *What's your SDD process?* → Minimal PRD → one use case spec with acceptance criteria → agent asks questions →
  implement with harness → verify → sync spec. Hybrid with vibe coding for exploratory work.
- *Is SDD just waterfall?* → Waterfall = long cycles with outdated requirements; SDD cycles are minutes. But
  specifying everything up front (software-factory style) does feel like waterfall.
