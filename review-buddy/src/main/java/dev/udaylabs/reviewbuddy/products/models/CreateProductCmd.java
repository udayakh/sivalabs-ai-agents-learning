package dev.udaylabs.reviewbuddy.products.models;

public record CreateProductCmd(
        String name,
        String tagline,
        String description,
        String websiteUrl,
        String imageUrl,
        Long categoryId,
        Long submittedBy) {}
