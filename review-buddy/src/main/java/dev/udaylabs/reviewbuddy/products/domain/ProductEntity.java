package dev.udaylabs.reviewbuddy.products.domain;

import dev.udaylabs.reviewbuddy.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
class ProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String tagline;

    @Column(nullable = false)
    private String description;

    @Column(name = "website_url", nullable = false)
    private String websiteUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    /** Reference to the users module by id only - no JPA association across module boundaries. */
    @Column(name = "submitted_by", nullable = false)
    private Long submittedBy;

    /** Denormalized rating stats, maintained by the reviews module via ProductApi. */
    @Column(name = "average_rating", nullable = false)
    private double averageRating;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    protected ProductEntity() {}

    ProductEntity(
            String name,
            String slug,
            String tagline,
            String description,
            String websiteUrl,
            String imageUrl,
            CategoryEntity category,
            Long submittedBy) {
        this.name = name;
        this.slug = slug;
        this.tagline = tagline;
        this.description = description;
        this.websiteUrl = websiteUrl;
        this.imageUrl = imageUrl;
        this.category = category;
        this.submittedBy = submittedBy;
    }

    void updateRatingStats(double averageRating, int reviewCount) {
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    Long getId() {
        return id;
    }

    String getName() {
        return name;
    }

    String getSlug() {
        return slug;
    }

    String getTagline() {
        return tagline;
    }

    String getDescription() {
        return description;
    }

    String getWebsiteUrl() {
        return websiteUrl;
    }

    String getImageUrl() {
        return imageUrl;
    }

    CategoryEntity getCategory() {
        return category;
    }

    Long getSubmittedBy() {
        return submittedBy;
    }

    double getAverageRating() {
        return averageRating;
    }

    int getReviewCount() {
        return reviewCount;
    }
}
