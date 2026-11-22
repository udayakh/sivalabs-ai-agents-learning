package dev.udaylabs.reviewbuddy.reviews.ai;

import dev.udaylabs.reviewbuddy.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "review_summaries")
class ReviewSummaryEntity extends BaseEntity {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(nullable = false)
    private String summary;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "generated_by", nullable = false)
    private String generatedBy;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    protected ReviewSummaryEntity() {}

    ReviewSummaryEntity(Long productId) {
        this.productId = productId;
    }

    void update(String summary, int reviewCount, String generatedBy, Instant generatedAt) {
        this.summary = summary;
        this.reviewCount = reviewCount;
        this.generatedBy = generatedBy;
        this.generatedAt = generatedAt;
    }

    Long getProductId() {
        return productId;
    }

    String getSummary() {
        return summary;
    }

    int getReviewCount() {
        return reviewCount;
    }

    String getGeneratedBy() {
        return generatedBy;
    }

    Instant getGeneratedAt() {
        return generatedAt;
    }
}
