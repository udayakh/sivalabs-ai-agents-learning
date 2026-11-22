# Code organization (Spring Modulith)

```
dev.udaylabs.reviewbuddy
├── ReviewBuddyApplication.java      main class in the ROOT package
├── config/                          global Spring config (security, auditing, async, error handling)
├── shared/                          OPEN module (package-info: @ApplicationModule(type = OPEN))
└── <module>/                        business module, e.g. products
    ├── <Module>Api.java             PUBLIC entry point for other modules (exposes a few service methods)
    ├── models/                      @NamedInterface("models"): public DTOs, commands, queries, events
    ├── domain/                      entities, repositories (package-private), services (public, used by web)
    ├── web/                         controllers + form records (package-private)
    └── ai/ | events/ | config/      optional: module specific listeners/integrations/config
```

Rules:
1. Another module may use ONLY `<module>/*Api` classes and the `models` named interface.
2. Never inject another module's service or repository. Add a method to its `*Api` instead.
3. Module dependencies must be acyclic. If A needs B and B needs A, introduce an event or move the feature
   (example: the product details page lives in `reviews.web` because it shows reviews; products never depends on reviews).
4. Cross-module communication that does not need an immediate answer -> publish an event (record in `models`),
   consume with `@ApplicationModuleListener`.
5. Reference entities of other modules by id (`Long submittedBy`), no `@ManyToOne` across modules.
