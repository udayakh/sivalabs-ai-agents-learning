# Prompts used for brainstorming and the PRD

> Course episode: "Brainstorming the Idea and Create PRD with AI"

## 1. Brainstorming prompt (skeptical co-founder / grill-me)

```text
Act as a skeptical co-founder who is about to invest time and money in my idea.
Idea: "Review Buddy" - a tiny Product Hunt style site where makers submit products they built,
users rate and review them (what they like / what to improve), and AI summarizes what people say.

Interview me ONE question at a time. Challenge: who exactly the users are, why they would use
this instead of existing products, what the smallest useful MVP is, and what we should NOT build.
Stop when you have enough clarity, then write a short brainstorming summary.
```

Tip: Matt Pocock's `grill-me` agent skill does the same job and is reusable.

## 2. PRD prompt (deliberately constrained)

```text
Act as an experienced product manager. Below is the brainstorming summary for Review Buddy.
Write docs/prd.md with ONLY these sections:
Overview, Problem Statement, Vision, Target Users, Key Features (MVP), Out of Scope, Risks & Assumptions.
Keep it under ~100 lines. Do NOT include technical details, architecture, tech stack,
detailed requirements, user flows, timelines or milestones.

<brainstorming summary here>
```

Why constrain the sections? Without them the AI happily generates thousands of lines (requirements,
flows, milestones, diagrams) that nobody reads and that become stale as soon as implementation starts.
