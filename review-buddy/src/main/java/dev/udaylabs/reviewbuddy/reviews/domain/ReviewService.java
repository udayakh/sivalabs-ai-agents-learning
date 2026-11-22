package dev.udaylabs.reviewbuddy.reviews.domain;

import dev.udaylabs.reviewbuddy.products.ProductApi;
import dev.udaylabs.reviewbuddy.products.models.ProductDto;
import dev.udaylabs.reviewbuddy.reviews.models.ReviewDto;
import dev.udaylabs.reviewbuddy.reviews.models.ReviewSubmittedEvent;
import dev.udaylabs.reviewbuddy.shared.BadRequestException;
import dev.udaylabs.reviewbuddy.users.UserApi;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductApi productApi;
    private final UserApi userApi;
    private final ApplicationEventPublisher events;

    ReviewService(
            ReviewRepository reviewRepository,
            ProductApi productApi,
            UserApi userApi,
            ApplicationEventPublisher events) {
        this.reviewRepository = reviewRepository;
        this.productApi = productApi;
        this.userApi = userApi;
        this.events = events;
    }

    public List<ReviewDto> getReviews(Long productId) {
        List<ReviewEntity> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        Map<Long, String> names = userApi.getUserNames(
                reviews.stream().map(ReviewEntity::getUserId).collect(Collectors.toSet()));
        return reviews.stream().map(r -> toDto(r, names.get(r.getUserId()))).toList();
    }

    public boolean hasReviewed(Long productId, Long userId) {
        return reviewRepository.existsByProductIdAndUserId(productId, userId);
    }

    /**
     * Business rules: a user can review a product only once and cannot review their own product.
     * After saving, the product's denormalized rating stats are refreshed (synchronously, same
     * transaction) and a {@link ReviewSubmittedEvent} is published for async processing.
     */
    @Transactional
    public ReviewDto submitReview(SubmitReviewCmd cmd) {
        ProductDto product = productApi.getById(cmd.productId());
        if (cmd.userId().equals(product.submittedBy())) {
            throw new BadRequestException("You cannot review your own product");
        }
        if (reviewRepository.existsByProductIdAndUserId(cmd.productId(), cmd.userId())) {
            throw new BadRequestException("You have already reviewed this product");
        }
        if (cmd.rating() < 1 || cmd.rating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }
        var review = reviewRepository.save(new ReviewEntity(
                cmd.productId(), cmd.userId(), cmd.rating(), cmd.likes().trim(), trimToNull(cmd.improvements())));

        long count = reviewRepository.countByProductId(cmd.productId());
        double average = reviewRepository.averageRating(cmd.productId());
        productApi.updateRatingStats(cmd.productId(), Math.round(average * 10) / 10.0, (int) count);

        events.publishEvent(new ReviewSubmittedEvent(cmd.productId(), review.getId()));
        return toDto(review, null);
    }

    private static String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static ReviewDto toDto(ReviewEntity r, String userName) {
        return new ReviewDto(
                r.getId(),
                r.getProductId(),
                r.getUserId(),
                userName,
                r.getRating(),
                r.getLikes(),
                r.getImprovements(),
                r.getCreatedAt());
    }
}
