package dev.udaylabs.reviewbuddy.reviews.ai;

import dev.udaylabs.reviewbuddy.reviews.models.ReviewDto;
import java.util.List;

/** Strategy for turning a list of reviews into a short human readable summary. */
interface ReviewSummarizer {

    ReviewSummaryResult summarize(String productName, List<ReviewDto> reviews);

    record ReviewSummaryResult(String summary, String generatedBy) {}
}
