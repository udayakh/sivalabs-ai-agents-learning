# Testing guidelines

- Integration tests extend `AbstractIntegrationTest` (`@SpringBootTest` + MockMvc + `test` profile, seeded H2).
- Use `@WithUserDetails("user@gmail.com")` for authenticated requests (loads our `SecurityUser`), `.with(csrf())` for POSTs.
- Test behaviour through HTTP (status, view, model attributes, rendered text), not private methods.
- Async listeners: assert with Awaitility (`await().untilAsserted(...)`), never `Thread.sleep`.
- LLM code: never call a real model in tests. Use a fake `ChatModel` with `ChatClient.create(fake)`.
- Pure logic (slugs, sorting, summarizer rules): plain JUnit 5 + AssertJ, parameterized where useful.
- Each test creates data with unique names so tests are independent of execution order.
- A good test verifies a business rule ("a user cannot review twice"), not just "returns 200".
