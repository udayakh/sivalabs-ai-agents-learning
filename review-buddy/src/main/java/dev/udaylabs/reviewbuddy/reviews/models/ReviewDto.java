package dev.udaylabs.reviewbuddy.reviews.models;

import java.time.Instant;

public record ReviewDto(
        Long id,
        Long productId,
        Long userId,
        String userName,
        int rating,
        String likes,
        String improvements,
        Instant createdAt) {}
