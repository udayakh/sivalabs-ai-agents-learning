package dev.udaylabs.reviewbuddy.reviews.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/** app.ai.summary.min-reviews: summaries are generated only once a product has this many reviews. */
@ConfigurationProperties("app.ai.summary")
record AiSummaryProperties(@DefaultValue("3") int minReviews) {}
