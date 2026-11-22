---
name: spring-modulith-verifier
description: Verify that the codebase follows our Spring Modulith code organization conventions and report violations. Use when asked to check, audit or verify module structure, package visibility or module boundaries.
---

# Spring Modulith verifier

1. Run `mvn -q test -Dtest=ModularityTest,ArchitectureTest,TaikaiArchitectureTest` and report failures first.
2. Then review each module against [../spring-boot/references/code-organization.md](../spring-boot/references/code-organization.md):
   - Are entities, repositories, controllers package-private?
   - Do other modules use only `*Api` classes and `models` types?
   - Any cross-module `@ManyToOne`, or a service injected from another module?
   - Naming: `*Controller`, `*Service`, `*Repository`, `*Config`, `*Api`, `*Dto`, `*Cmd`, `*Event`.
3. Output a table: `Rule | Status (OK / VIOLATION) | Location | Suggested fix`.
4. Do NOT change code unless explicitly asked. If asked to fix, fix, run `mvn spotless:apply && mvn verify`.
5. If you find a recurring violation that tests did not catch, propose a new ArchUnit/Taikai rule.
