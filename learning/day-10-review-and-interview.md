# Day 10: Review, opinions, and the mock interview

## Goal
Consolidate everything and form *your own* opinions; interviewers love reasoned trade-offs.

## Key ideas from the final episode
- **Should we still read/review code?** Yes, for long-lived apps. Agents tend to generate overly complex code
  ("12 steps where 5 would do"). A harness doesn't catch over-engineering or wrong business logic. Skip review only
  for throwaway personal utilities.
- **Which ADE?** Many vibe-coded ADEs appear and may not be maintained in 6 months. Stick to a few solid ones
  (Codex, Claude, IntelliJ + Air) and stay curious.
- **Software factory** (requirements in → finished app out, agents in a loop): didn't work in the course author's
  experience. One wrong assumption sends the agent in a wrong direction; you drift away from the design decisions;
  it resembles upfront waterfall planning. Prefer **one spec at a time**, keep yourself in the loop.
- Keep the process simple. 27 chained skills are not a process; a clear spec + harness + review is.

## Hands-on: review this codebase like a senior
Review `review-buddy` and write 5 findings. Ideas to look for:
1. `ReviewSummaryService.regenerateSummary` holds a DB transaction during the LLM call. Trade-off? Alternative?
2. Rating stats are recomputed with 2 queries per review. Concurrency issue if two reviews arrive at the same time? (Hint: `@Version` on the product.)
3. `ProductService.uniqueSlug` checks existence in a loop: race condition? (DB unique constraint saves us; how to handle the exception?)
4. Tests use H2 in PostgreSQL mode: what could differ from real PostgreSQL? Add Testcontainers when Docker is available.
5. Which tests are superficial, and which assert business rules?

## Mock interview (answer out loud, 2 minutes each)
1. Walk me through the architecture of a project you built recently.
2. How do you work with AI coding agents? How do you ensure quality?
3. What is harness engineering? Give concrete examples.
4. Explain Spring Modulith. How do modules communicate? How do you prevent cycles?
5. How did you integrate an LLM? What happens if OpenAI is down?
6. How do you test code that calls an LLM?
7. Spec-driven development vs vibe coding: when do you use which?
8. What are agent skills? Why not put everything in AGENTS.md?
9. How do you give agents knowledge about libraries newer than their training data?
10. What's your opinion on "we don't need to read code anymore"?

Model answers: [interview-questions.md](interview-questions.md).

## You're done when
- `mvn verify` is green with UC-006 implemented by you + an agent.
- You can whiteboard the module diagram and the review → event → summary flow.
- You can answer the 10 questions above without notes.
