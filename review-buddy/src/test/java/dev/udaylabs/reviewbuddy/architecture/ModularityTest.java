package dev.udaylabs.reviewbuddy.architecture;

import dev.udaylabs.reviewbuddy.ReviewBuddyApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Harness: Spring Modulith verifies module boundaries. Fails the build when a module uses
 * another module's internal (nested package) types or when module dependencies form a cycle.
 */
class ModularityTest {

    static final ApplicationModules modules = ApplicationModules.of(ReviewBuddyApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        // Generates PlantUML component diagrams + module canvases into target/spring-modulith-docs
        new Documenter(modules).writeDocumentation();
    }
}
