package dev.udaylabs.reviewbuddy.products.domain;

import dev.udaylabs.reviewbuddy.products.models.CategoryDto;
import dev.udaylabs.reviewbuddy.products.models.ProductDto;

final class ProductMapper {

    private ProductMapper() {}

    static CategoryDto toDto(CategoryEntity category) {
        return new CategoryDto(category.getId(), category.getName(), category.getSlug());
    }

    static ProductDto toDto(ProductEntity product, String submittedByName) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getTagline(),
                product.getDescription(),
                product.getWebsiteUrl(),
                product.getImageUrl(),
                toDto(product.getCategory()),
                product.getSubmittedBy(),
                submittedByName,
                product.getAverageRating(),
                product.getReviewCount(),
                product.getCreatedAt());
    }
}
