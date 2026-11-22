package dev.udaylabs.reviewbuddy.reviews.models;

import java.time.Instant;

/** AI (or rule based fallback) generated summary of all reviews of a product. */
public record ReviewSummaryDto(
        Long productId, String summary, int reviewCount, String generatedBy, Instant generatedAt) {}
