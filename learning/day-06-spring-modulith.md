# Day 6: Modular monolith with Spring Modulith

## Goal
Be able to explain and defend the code organization of Review Buddy.

## Key ideas
- **Package by feature/module**, not by layer. Each top-level package under the main class's package = a module.
- Public types in a module's **root package** are its API; **sub-packages are internal**: even public classes there
  can't be used by other modules (Modulith's `verify()` fails the build, even though javac allows it).
- **Named interfaces** (`@NamedInterface("models")` on `package-info.java`) expose selected sub-packages (DTOs, events).
- **OPEN modules** (`@ApplicationModule(type = OPEN)`) for shared kernels.
- Prefer an `XxxApi` facade over exposing services: other modules get just the methods they need.
- **No cycles**. Use events for "tell, don't ask" communication.
- `@ApplicationModuleListener` = `@Async` + `@Transactional(REQUIRES_NEW)` + `@TransactionalEventListener(AFTER_COMMIT)`.
- **Event publication registry** stores events in `event_publication`; incomplete ones can be republished → reliable
  async processing (transactional outbox).
- Flat packages are also fine. Nested packages + named interfaces are a preference, not "the best way".

## In this repo
- `shared/package-info.java` (OPEN), `users/models/package-info.java`, `products/models/package-info.java` (named interfaces).
- `products/ProductApi.java`, `users/UserApi.java`: narrow facades.
- `reviews/web/ProductDetailsController.java`: why the details page lives in *reviews* (avoids products ↔ reviews cycle).
- `reviews/domain/ReviewService.java` publishes `ReviewSubmittedEvent`; `reviews/ai/ReviewSummaryEventListener.java` consumes it.
- `src/test/.../architecture/ModularityTest.java` → after `mvn test` see `target/spring-modulith-docs/*.puml`.

## Hands-on
1. Run `mvn test -Dtest=ModularityTest`, open `target/spring-modulith-docs/components.puml` in a PlantUML viewer.
2. Break it: in `ProductController` inject `ReviewService` (another module's internal class). Run the test, read the error, revert.
3. Break it differently: make `ProductService` call `ReviewSummaryService` → you create a cycle. Read the error, revert.
4. Explain on paper: what happens if the app crashes after the review commits but before the summary is generated?

## Interview questions
- *Modular monolith vs microservices?* → Same boundaries and independence of code, without distributed-system cost
  (network, deployments, data consistency). Modules can be extracted later if needed.
- *How do modules communicate?* → Synchronously via small API facades for queries/commands needing an answer;
  asynchronously via domain events for side effects.
- *How do you guarantee an event is processed?* → Modulith event publication registry (outbox) + republish on restart.
