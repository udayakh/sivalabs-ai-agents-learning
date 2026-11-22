package dev.udaylabs.reviewbuddy.products.models;

import java.time.Instant;

public record ProductDto(
        Long id,
        String name,
        String slug,
        String tagline,
        String description,
        String websiteUrl,
        String imageUrl,
        CategoryDto category,
        Long submittedBy,
        String submittedByName,
        double averageRating,
        int reviewCount,
        Instant createdAt) {

    /** Rounded rating used to render the stars (0..5). */
    public int stars() {
        return (int) Math.round(averageRating);
    }
}
