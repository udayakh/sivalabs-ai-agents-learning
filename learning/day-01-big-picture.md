# Day 1: The big picture

## Goal
Understand *why* the course exists, what we build, and get the app running.

## Key ideas from the course
- **AI is an amplifier, not a replacement.** At work you are still *accountable* for code you ship. "I don't read code
  anymore" claims online don't match a real job.
- There is **no silver bullet**: the right process depends on the project's risk. A personal utility can be vibe coded;
  a long-lived business app needs specs, harness and review.
- Workflow of the course: **Idea → Brainstorm → PRD → Scaffold → IDE/ADE → Mock-up → Agent skills → Harness → Spec-driven features → Latest tech (Spring AI) → Review**.
- The app (Review Buddy) is intentionally small; the *process* is the subject.

## Hands-on (60 min)
1. `cd review-buddy && mvn verify`: read the output and find: number of tests, the JaCoCo line, the Spotless line.
2. `mvn spring-boot:run` → http://localhost:8080
   - Browse, search "pixel", filter "Artificial Intelligence", sort "Top rated", page 2.
   - Register a new user, submit a product.
   - Log in as `bob@gmail.com / secret`, open **Progen**, write the 3rd review → the AI summary appears.
3. Open `review-buddy/README.md` and read "Key design decisions".
4. Skim the package tree under `src/main/java/dev/udaylabs/reviewbuddy`. Note the modules: users, products, reviews.

## Interview questions
- *How do you use AI agents in your daily development?* → Amplifier: brainstorm, spec, generate, but with
  deterministic guardrails (tests, architecture rules) and human review of critical business logic and tests.
- *Would you skip code review for AI generated code?* → Depends on risk. For maintainable business apps, no:
  agents over-engineer and occasionally break rules; harness catches structure, humans verify logic and test quality.

## Checkpoint
You can demo the full user journey and explain the 10 steps of the workflow in your own words.
