package dev.udaylabs.reviewbuddy.reviews.ai;

import dev.udaylabs.reviewbuddy.reviews.models.ReviewDto;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Deterministic, rule based summarizer. Used when no LLM is configured (local dev, tests, CI) and
 * as a fallback when the LLM call fails, so the feature degrades gracefully instead of breaking.
 */
class SimpleReviewSummarizer implements ReviewSummarizer {

    static final String GENERATED_BY = "rule-based";

    private static final Set<String> STOP_WORDS = Set.of(
            "the", "and", "for", "with", "that", "this", "very", "really", "it's", "its", "are", "was", "but", "have",
            "has", "just", "more", "much", "from", "they", "would", "could", "should", "like", "love", "nice", "good",
            "great", "also", "when", "what", "some", "into", "than", "then", "there", "their", "your", "you", "can",
            "not", "too", "all", "any", "out", "use", "using", "used", "make", "makes", "need", "needs", "will",
            "being", "been", "were", "which", "while", "about", "over", "only", "even", "most", "less", "lot", "lots",
            "add");

    @Override
    public ReviewSummaryResult summarize(String productName, List<ReviewDto> reviews) {
        if (reviews.isEmpty()) {
            return new ReviewSummaryResult("No reviews yet for " + productName + ".", GENERATED_BY);
        }
        double average = reviews.stream().mapToInt(ReviewDto::rating).average().orElse(0);
        String praised = topKeywords(reviews.stream().map(ReviewDto::likes).toList());
        String improvements =
                topKeywords(reviews.stream().map(ReviewDto::improvements).toList());

        var sb = new StringBuilder();
        sb.append("Based on %d reviews (average %.1f/5), feedback on %s is %s."
                .formatted(reviews.size(), average, productName, sentiment(average)));
        if (!praised.isEmpty()) {
            sb.append(" Reviewers most often mention: ").append(praised).append('.');
        }
        sb.append(
                improvements.isEmpty()
                        ? " No major improvement requests so far."
                        : " Suggested improvements focus on: " + improvements + ".");
        return new ReviewSummaryResult(sb.toString(), GENERATED_BY);
    }

    static String sentiment(double average) {
        if (average >= 4.0) {
            return "very positive";
        }
        if (average >= 3.0) {
            return "mostly positive";
        }
        if (average >= 2.0) {
            return "mixed";
        }
        return "mostly negative";
    }

    static String topKeywords(Collection<String> texts) {
        Map<String, Long> frequencies = texts.stream()
                .filter(Objects::nonNull)
                .flatMap(t -> Arrays.stream(t.toLowerCase(Locale.ROOT).split("[^a-z0-9']+")))
                .filter(w -> w.length() > 2 && !STOP_WORDS.contains(w))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return frequencies.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));
    }
}
