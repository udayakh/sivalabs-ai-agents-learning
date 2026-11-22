package dev.udaylabs.reviewbuddy.products.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

record ProductForm(
        @NotBlank(message = "Name is required") @Size(max = 100, message = "Name must be at most 100 characters") String name,

        @NotBlank(message = "Tagline is required") @Size(max = 150, message = "Tagline must be at most 150 characters") String tagline,

        @NotBlank(message = "Description is required") @Size(max = 2000, message = "Description must be at most 2000 characters") String description,

        @NotBlank(message = "Website URL is required") @Pattern(regexp = "^https?://.+", message = "Website URL must start with http:// or https://") String websiteUrl,

        @Pattern(regexp = "^$|^https?://.+", message = "Image URL must start with http:// or https://") String imageUrl,

        @NotNull(message = "Category is required") Long categoryId) {

    static ProductForm empty() {
        return new ProductForm("", "", "", "", "", null);
    }
}
