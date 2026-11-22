package dev.udaylabs.reviewbuddy.reviews.domain;

public record SubmitReviewCmd(Long productId, Long userId, int rating, String likes, String improvements) {}
