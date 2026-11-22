package dev.udaylabs.reviewbuddy.reviews.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

record ReviewForm(
        @NotNull(message = "Rating is required") @Min(value = 1, message = "Rating must be 1-5") @Max(value = 5, message = "Rating must be 1-5") Integer rating,

        @NotBlank(message = "Tell us what you like") @Size(max = 1000, message = "At most 1000 characters") String likes,

        @Size(max = 1000, message = "At most 1000 characters") String improvements) {

    static ReviewForm empty() {
        return new ReviewForm(5, "", "");
    }
}
