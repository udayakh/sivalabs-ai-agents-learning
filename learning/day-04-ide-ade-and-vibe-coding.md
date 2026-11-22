# Day 4: IDE + ADE, vibe coding mock-ups, Playwright MCP

## Goal
Set up an agentic development environment and learn when vibe coding is the right tool.

## Key ideas
- **ADE** = agentic development environment (Codex app, Claude desktop/Code, Cursor, JetBrains Air…).
  The course uses **IntelliJ IDEA + JetBrains Air** as both IDE and ADE: one tool for prompting, reviewing diffs and
  editing; switch models (Codex/Claude/Junie) without learning a new UI. Air integrates agents via **ACP** (Agent Client Protocol).
- Useful IntelliJ plugins: Spring Debugger, JPA Buddy, Laconic POM, Dependency Scout, File Nesting.
  Install IDEs via JetBrains Toolbox; log in to GitHub with a longer-lived token.
- **Vibe coding is not good or bad, it depends on the risk.** Great for mock-ups, prototypes and idea validation.
- **Mock-up first**: iterate on static HTML + Tailwind CDN (fast) instead of the real app (restart cycles), then
  tell the agent "make this page look like `mockup/products.html`".
- **Playwright MCP** lets the agent open the page, take screenshots and fix the UI itself.

## In this repo
- Templates use the green/white Tailwind style a mock-up would define: `templates/fragments/layout.html`,
  `products/list.html`, `products/details.html`. The `mockup/` folder is git-ignored (as in the course).
- `.mcp.json.example` contains Playwright and Context7.

## Hands-on
1. Copy `.mcp.json.example` to `.mcp.json` (needs Node.js; Context7 needs a free API key) and open the project
   in your agent (Claude Code / Codex / Air).
2. Vibe code a mock-up: *"Create mockup/me.html, a static page for 'My products and my reviews' using Tailwind CDN,
   green/white theme like templates/products/list.html, sample data."* Iterate 2–3 times. You will need it on Day 8.
3. Ask the agent to use Playwright MCP to screenshot http://localhost:8080/products and suggest UI improvements.

## Interview questions
- *When is vibe coding acceptable?* → Prototypes, mock-ups, validating a business idea, personal tools. Not for
  long-lived critical code without specs, tests and review.
- *Why make a mock-up before implementing?* → Faster UI iteration; becomes a visual spec the agent can follow.
