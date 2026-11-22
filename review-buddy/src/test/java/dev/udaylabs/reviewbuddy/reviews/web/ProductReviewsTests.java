package dev.udaylabs.reviewbuddy.reviews.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.udaylabs.reviewbuddy.AbstractIntegrationTest;
import dev.udaylabs.reviewbuddy.products.ProductApi;
import dev.udaylabs.reviewbuddy.products.models.ProductDto;
import dev.udaylabs.reviewbuddy.reviews.ai.ReviewSummaryService;
import dev.udaylabs.reviewbuddy.reviews.models.ReviewSummaryDto;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;

class ProductReviewsTests extends AbstractIntegrationTest {

    @Autowired
    ProductApi productApi;

    @Autowired
    ReviewSummaryService reviewSummaryService;

    @Test
    void anonymousUserSeesProductDetailsAndReviews() throws Exception {
        mockMvc.perform(get("/products/pixelpress"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("PixelPress")))
                .andExpect(content().string(containsString("What people are saying")))
                .andExpect(content().string(containsString("Log in to write a review.")))
                .andExpect(model().attribute("canReview", false));
    }

    @Test
    void anonymousUserCannotPostReview() throws Exception {
        mockMvc.perform(post("/products/focusflow/reviews")
                        .with(csrf())
                        .param("rating", "5")
                        .param("likes", "nice"))
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    void makerCannotReviewOwnProduct() throws Exception {
        mockMvc.perform(get("/products/progen"))
                .andExpect(model().attribute("canReview", false))
                .andExpect(content().string(containsString("You submitted this product")));

        mockMvc.perform(post("/products/progen/reviews")
                        .with(csrf())
                        .param("rating", "5")
                        .param("likes", "my own product"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }

    @Test
    @WithUserDetails("user@gmail.com")
    void userCannotReviewSameProductTwice() throws Exception {
        mockMvc.perform(get("/products/progen"))
                .andExpect(model().attribute("canReview", false))
                .andExpect(content().string(containsString("already reviewed")));

        mockMvc.perform(post("/products/progen/reviews")
                        .with(csrf())
                        .param("rating", "1")
                        .param("likes", "again"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }

    @Test
    @WithUserDetails("alice@gmail.com")
    void validatesReviewForm() throws Exception {
        mockMvc.perform(post("/products/launchboard/reviews")
                        .with(csrf())
                        .param("rating", "9")
                        .param("likes", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "rating", "likes"));
    }

    @Test
    @WithUserDetails("bob@gmail.com")
    void thirdReviewUpdatesRatingAndTriggersAsyncSummary() throws Exception {
        assertThat(reviewSummaryService.findSummary(progen().id())).isEmpty();

        mockMvc.perform(post("/products/progen/reviews")
                        .with(csrf())
                        .param("rating", "3")
                        .param("likes", "Flyway and Spotless defaults are handy")
                        .param("improvements", "Needs Gradle support"))
                .andExpect(redirectedUrl("/products/progen#reviews"));

        ProductDto progen = progen();
        assertThat(progen.reviewCount()).isEqualTo(3);
        assertThat(progen.averageRating()).isEqualTo(4.0);

        // The summary is generated asynchronously by the @ApplicationModuleListener after commit
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            ReviewSummaryDto summary =
                    reviewSummaryService.findSummary(progen.id()).orElseThrow();
            assertThat(summary.reviewCount()).isEqualTo(3);
            assertThat(summary.generatedBy()).isEqualTo("rule-based");
            assertThat(summary.summary()).contains("3 reviews").containsIgnoringCase("gradle");
        });
    }

    private ProductDto progen() {
        return productApi.findBySlug("progen").orElseThrow();
    }
}
