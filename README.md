# Building with AI Agents: Learning Workspace

Start here. This workspace turns the course *"Building with AI Agents: From Idea to Production"*
(Spring Boot + IntelliJ + AI agents) into a working project and a 10-day learning plan for interviews.

```
sivalabs-ai-agents-learning/
├── README.md                ← you are here
├── review-buddy/            ← the finished application (code, docs, agent skills, harness)
│   └── README.md            ← architecture, design decisions, how it was built
└── learning/
    ├── day-01 … day-10      ← one topic per day: concepts → where in the code → hands-on → interview Q&A
    └── interview-questions.md
```

## Quick start (5 minutes)

Requirements: Java 21+, Maven 3.9+. Docker and an OpenAI key are optional.

```bash
cd review-buddy
mvn verify            # 49 tests + architecture rules + coverage + formatting -> BUILD SUCCESS
mvn spring-boot:run   # open http://localhost:8080 , login user@gmail.com / user
```

## 10-day plan (≈ 1.5-2 hours per day)

| Day | Topic | Course episode |
|---|---|---|
| [01](learning/day-01-big-picture.md) | The big picture, run the app, tour the code | Course announcement, AI as an amplifier |
| [02](learning/day-02-brainstorm-and-prd.md) | Brainstorming with AI and writing a minimal PRD | Brainstorming & PRD |
| [03](learning/day-03-scaffolding.md) | Scaffolding a production-grade Spring Boot app | Progen CLI |
| [04](learning/day-04-ide-ade-and-vibe-coding.md) | IDE + ADE setup, vibe coding mock-ups, Playwright MCP | IntelliJ + Air, vibe coding |
| [05](learning/day-05-agent-skills.md) | Agent skills and AGENTS.md | Agent skills |
| [06](learning/day-06-spring-modulith.md) | Modular monolith with Spring Modulith | Harness (part 1) |
| [07](learning/day-07-harness-engineering.md) | Harness engineering: Spotless, JaCoCo, ArchUnit, Taikai | Harness engineering |
| [08](learning/day-08-spec-driven-development.md) | Spec-driven development, implement a use case yourself | Spec-driven development |
| [09](learning/day-09-spring-ai-and-latest-tech.md) | Spring AI, events, Context7 / llms.txt / docs MCP | Latest technologies |
| [10](learning/day-10-review-and-interview.md) | Code review, software-factory debate, mock interview | Final episode |

Then use [learning/interview-questions.md](learning/interview-questions.md) for revision.

## The one-paragraph pitch (memorize it)

> "I built Review Buddy, a Spring Boot 4 modular monolith, using AI coding agents in a disciplined way.
> I brainstormed and wrote a minimal PRD with AI, scaffolded with deterministic tools instead of spending tokens,
> taught the agent my conventions with agent skills, and enforced them with harness tests: Spring Modulith
> boundary checks, ArchUnit and Taikai rules, JaCoCo coverage gates and Spotless formatting. Features were built
> one spec at a time. The AI review summary uses Spring AI's ChatClient behind an interface, runs asynchronously from a
> Modulith event after commit, retries, and falls back to a rule-based summarizer, so the app works with no
> API key and the LLM is fully mocked in tests."
