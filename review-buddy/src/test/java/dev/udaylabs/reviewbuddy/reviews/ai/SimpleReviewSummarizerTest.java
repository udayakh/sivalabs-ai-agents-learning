package dev.udaylabs.reviewbuddy.reviews.ai;

import static org.assertj.core.api.Assertions.assertThat;

import dev.udaylabs.reviewbuddy.reviews.models.ReviewDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SimpleReviewSummarizerTest {

    private final SimpleReviewSummarizer summarizer = new SimpleReviewSummarizer();

    @Test
    void summarizesRatingsPraiseAndImprovements() {
        var reviews = List.of(
                review(5, "Fast export and clean UI", "Dark mode"),
                review(4, "Export is fast", "Dark mode and pricing"),
                review(3, "Clean UI", null));

        var result = summarizer.summarize("PixelPress", reviews);

        assertThat(result.generatedBy()).isEqualTo("rule-based");
        assertThat(result.summary())
                .startsWith("Based on 3 reviews (average 4.0/5), feedback on PixelPress is very positive.")
                .contains("Reviewers most often mention: clean, export, fast")
                .contains("Suggested improvements focus on: dark, mode, pricing");
    }

    @Test
    void reportsNoImprovementsWhenNoneGiven() {
        var result = summarizer.summarize("X", List.of(review(5, "Great stuff", null)));
        assertThat(result.summary()).contains("No major improvement requests so far.");
    }

    @Test
    void handlesNoReviews() {
        assertThat(summarizer.summarize("X", List.of()).summary()).isEqualTo("No reviews yet for X.");
    }

    @ParameterizedTest
    @CsvSource({"4.5, very positive", "3.2, mostly positive", "2.5, mixed", "1.0, mostly negative"})
    void mapsAverageToSentiment(double average, String expected) {
        assertThat(SimpleReviewSummarizer.sentiment(average)).isEqualTo(expected);
    }

    static ReviewDto review(int rating, String likes, String improvements) {
        return new ReviewDto(1L, 1L, 1L, "u", rating, likes, improvements, null);
    }
}
