package dev.udaylabs.reviewbuddy.reviews.models;

/**
 * Domain event published after a review is stored. Consumed asynchronously (after commit) to
 * regenerate the AI review summary, so the user never waits for the LLM call.
 */
public record ReviewSubmittedEvent(Long productId, Long reviewId) {}
