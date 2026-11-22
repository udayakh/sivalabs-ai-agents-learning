package dev.udaylabs.reviewbuddy.shared;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SlugsTest {

    @ParameterizedTest
    @CsvSource(
            delimiter = '|',
            value = {
                "Pixel Press|pixel-press",
                "  Hello,   World!!  |hello-world",
                "Café Déjà Vu|cafe-deja-vu",
                "Spring Boot 4.1|spring-boot-4-1",
                "---|''"
            })
    void slugifies(String input, String expected) {
        assertThat(Slugs.slugify(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"''", "'   '"})
    void blankInputGivesEmptySlug(String input) {
        assertThat(Slugs.slugify(input)).isEmpty();
    }
}
