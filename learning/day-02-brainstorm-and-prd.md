# Day 2: Brainstorming and the PRD

## Goal
Learn to use AI to *question* an idea before building it, and to produce a short, useful PRD.

## Key ideas
- Building is cheap now; **choosing the right thing to build** is the hard part.
- LLMs flatter you by default. Ask them to act as a **skeptical co-founder** and ask **one question at a time**
  (or use Matt Pocock's `grill-me` skill).
- The **PRD is shared context** for humans and agents: overview, problem, vision, users, MVP features, out of scope,
  risks. It also prevents over-engineering ("target users are indie makers" → no need for millions-of-users design).
- **No technical details** in the PRD (a non-technical co-founder must understand it).
- **Constrain the sections**, or the AI writes 2,000+ lines nobody reads. Start minimal and add sections when needed.
- Don't put detailed requirements in the PRD: their order and content change once you start implementing.

## In this repo
- `review-buddy/docs/prd-prompt.md`: both prompts.
- `review-buddy/docs/prd.md`: the resulting ~50 line PRD.

## Hands-on
1. Pick your own small idea (e.g. "team lunch voting app"). Run the skeptical co-founder prompt in any AI chat.
   Answer at least 8 questions.
2. Generate a PRD **without** section constraints, then **with** them. Compare length and usefulness.
3. Write down 3 things the AI made you reconsider.

## Interview questions
- *How do you start a new product/feature with AI?* → Brainstorm adversarially, write a minimal PRD as shared context,
  then derive use cases one at a time.
- *What belongs in a PRD vs a spec?* → PRD = why/what/for whom (business). Spec/use case = detailed behaviour and
  acceptance criteria for one feature. Technical design = separate.
