package dev.udaylabs.reviewbuddy.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

/**
 * Harness: architectural rules expressed as executable tests (ArchUnit). Agent skills only
 * *suggest* conventions; these tests *enforce* them deterministically on every build.
 */
@AnalyzeClasses(packages = ArchitectureTest.BASE_PACKAGE, importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    static final String BASE_PACKAGE = "dev.udaylabs.reviewbuddy";

    @ArchTest
    static final ArchRule entitiesMustNotBePublic = classes()
            .that()
            .areAnnotatedWith(Entity.class)
            .should()
            .notBePublic()
            .because("JPA entities must never leak outside their module; expose DTOs instead");

    @ArchTest
    static final ArchRule repositoriesMustNotBePublic = classes()
            .that()
            .haveSimpleNameEndingWith("Repository")
            .should()
            .notBePublic()
            .because("only the module's own services may access its repositories");

    @ArchTest
    static final ArchRule controllersMustNotBePublic =
            classes().that().areAnnotatedWith(Controller.class).should().notBePublic();

    @ArchTest
    static final ArchRule servicesMustOnlyBeUsedWithinTheirModule = classes()
            .that()
            .areAnnotatedWith(Service.class)
            .should(beAccessedOnlyFromTheirOwnModule())
            .because("other modules must go through the module's *Api class");

    @ArchTest
    static final ArchRule domainMustNotDependOnWeb = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..web..");

    @ArchTest
    static final ArchRule webMustNotDependOnEntities = noClasses()
            .that()
            .resideInAPackage("..web..")
            .should()
            .dependOnClassesThat()
            .areAnnotatedWith(Entity.class)
            .because("controllers and views work with DTO records, never with JPA entities");

    @ArchTest
    static final ArchRule domainMustNotDependOnServletApi = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("jakarta.servlet..", "org.springframework.web..", "org.springframework.ui..");

    private static ArchCondition<JavaClass> beAccessedOnlyFromTheirOwnModule() {
        return new ArchCondition<>("be accessed only from classes of their own module") {
            @Override
            public void check(JavaClass service, ConditionEvents events) {
                String serviceModule = moduleOf(service);
                for (Dependency dependency : service.getDirectDependenciesToSelf()) {
                    JavaClass origin = dependency.getOriginClass();
                    if (origin.getPackageName().startsWith(BASE_PACKAGE) && !serviceModule.equals(moduleOf(origin))) {
                        events.add(SimpleConditionEvent.violated(dependency, dependency.getDescription()));
                    }
                }
            }
        };
    }

    /** dev.udaylabs.reviewbuddy.products.domain.X -> "products". */
    static String moduleOf(JavaClass javaClass) {
        String pkg = javaClass.getPackageName();
        if (pkg.length() <= BASE_PACKAGE.length()) {
            return "";
        }
        String rest = pkg.substring(BASE_PACKAGE.length() + 1);
        int dot = rest.indexOf('.');
        return dot < 0 ? rest : rest.substring(0, dot);
    }
}
