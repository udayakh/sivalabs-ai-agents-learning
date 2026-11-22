# Day 5: Agent skills and AGENTS.md

## Goal
Teach an agent *your* conventions in a token-efficient way.

## Key ideas
- An **agent skill** = folder with `SKILL.md` (frontmatter `name` + `description`) + optional reference files/scripts.
  - It can be **guidelines** (how we write controllers) or a **capability** (scripts/CLIs, e.g. generate thumbnails).
- **Why not put everything in AGENTS.md / CLAUDE.md?** Those are sent with *every* request (tokens!). Skills are
  **discovered dynamically**: the agent reads only descriptions and loads a skill when relevant.
  → The **description is the most important part**: it decides when the skill triggers.
- Split big skills into reference files (progressive disclosure).
- Invoke explicitly: Claude Code `/skill-name`, Codex `$skill-name`. Install shared skills with `npx skills add <repo>`
  (project or global; `.agents/skills` + symlink to `.claude/skills`).
- Agents also **imitate existing code**: in the course, the agent copied the existing flat package structure even
  though the skill said nested packages. Skills are **suggestions, not guarantees** → Day 7 (harness).
- Review third-party skills before installing; clone and adapt to your preferences.
- Popular skills: SivaLabs agent skills, JetBrains skills, Matt Pocock (`grill-me`), Addy Osmani, Spring Boot skills by Piotr.

## In this repo
- `review-buddy/AGENTS.md` (short: commands, non-negotiables, workflow, doc sources) + `CLAUDE.md` (imports it).
- `.agents/skills/spring-boot/SKILL.md` + `references/code-organization.md`, `references/testing.md`.
- `.agents/skills/spring-modulith-verifier`, `prd-to-requirements`, `implement-use-case`, `update-use-case`.

## Hands-on
1. Read `spring-boot/SKILL.md`. Find 3 rules and locate code that follows each one.
2. Ask your agent: *"Use the spring-modulith-verifier skill to audit the codebase."* Compare its report to the tests.
3. Write your own skill `.agents/skills/thymeleaf-forms/SKILL.md` describing how forms/validation errors are rendered
   (look at `products/new.html`). Pay attention to the description.

## Interview questions
- *What is an agent skill and how is it different from AGENTS.md?* → On-demand, description-triggered instructions
  and tools vs always-loaded context. Saves tokens and keeps context focused.
- *If you have skills, why do you still need architecture tests?* → Skills guide; tests enforce. LLMs are
  probabilistic and goal-oriented; they deviate when blocked.
