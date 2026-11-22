package dev.udaylabs.reviewbuddy.reviews.domain;

import dev.udaylabs.reviewbuddy.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews")
class ReviewEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private String likes;

    private String improvements;

    protected ReviewEntity() {}

    ReviewEntity(Long productId, Long userId, int rating, String likes, String improvements) {
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.likes = likes;
        this.improvements = improvements;
    }

    Long getId() {
        return id;
    }

    Long getProductId() {
        return productId;
    }

    Long getUserId() {
        return userId;
    }

    int getRating() {
        return rating;
    }

    String getLikes() {
        return likes;
    }

    String getImprovements() {
        return improvements;
    }
}
