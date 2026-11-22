package dev.udaylabs.reviewbuddy.architecture;

import com.enofex.taikai.Taikai;
import org.junit.jupiter.api.Test;

/**
 * Harness: Taikai (built on ArchUnit) ships ready made Java / Spring conventions, so we do not
 * have to hand-write every rule.
 */
class TaikaiArchitectureTest {

    @Test
    void shouldFollowJavaAndSpringConventions() {
        Taikai.builder()
                .namespace("dev.udaylabs.reviewbuddy")
                // HttpSecurity#build() forces "throws Exception": a pragmatic, documented exclusion
                .excludeClasses("dev.udaylabs.reviewbuddy.config.WebSecurityConfig")
                .java(java -> java.noUsageOfDeprecatedAPIs()
                        .methodsShouldNotDeclareGenericExceptions()
                        .utilityClassesShouldBeFinalAndHavePrivateConstructor()
                        .noUsageOfSystemOutOrErr()
                        .fieldsShouldNotBePublic())
                .spring(spring -> spring.noAutowiredFields()
                        .boot(boot -> boot.applicationClassShouldResideInPackage("dev.udaylabs.reviewbuddy"))
                        .configurations(config -> config.namesShouldMatch(".*Config"))
                        .controllers(controllers -> controllers
                                .shouldBeAnnotatedWithController()
                                .namesShouldEndWithController()
                                .shouldNotDependOnOtherControllers()
                                .shouldNotDependOnRepositories()
                                .shouldBePackagePrivate())
                        .services(services -> services.shouldBeAnnotatedWithService()
                                .namesShouldMatch(".*Service")
                                .shouldNotDependOnControllers())
                        .repositories(repositories -> repositories
                                .namesShouldEndWithRepository()
                                .shouldNotDependOnServices()
                                .shouldBePackagePrivate()))
                .build()
                .check();
    }
}
