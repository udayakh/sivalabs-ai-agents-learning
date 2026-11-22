package dev.udaylabs.reviewbuddy.reviews.ai;

import dev.udaylabs.reviewbuddy.products.ProductApi;
import dev.udaylabs.reviewbuddy.reviews.domain.ReviewService;
import dev.udaylabs.reviewbuddy.reviews.models.ReviewDto;
import dev.udaylabs.reviewbuddy.reviews.models.ReviewSummaryDto;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewSummaryService {

    private final ReviewSummaryRepository summaryRepository;
    private final ReviewService reviewService;
    private final ProductApi productApi;
    private final ReviewSummarizer summarizer;
    private final AiSummaryProperties properties;

    ReviewSummaryService(
            ReviewSummaryRepository summaryRepository,
            ReviewService reviewService,
            ProductApi productApi,
            ReviewSummarizer summarizer,
            AiSummaryProperties properties) {
        this.summaryRepository = summaryRepository;
        this.reviewService = reviewService;
        this.productApi = productApi;
        this.summarizer = summarizer;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public Optional<ReviewSummaryDto> findSummary(Long productId) {
        return summaryRepository.findById(productId).map(ReviewSummaryService::toDto);
    }

    public int minReviewsForSummary() {
        return properties.minReviews();
    }

    /** (Re)generates the summary once the product has at least {@code min-reviews} reviews. */
    @Transactional
    public Optional<ReviewSummaryDto> regenerateSummary(Long productId) {
        List<ReviewDto> reviews = reviewService.getReviews(productId);
        if (reviews.size() < properties.minReviews()) {
            return Optional.empty();
        }
        String productName = productApi.getById(productId).name();
        var result = summarizer.summarize(productName, reviews);
        var entity = summaryRepository.findById(productId).orElseGet(() -> new ReviewSummaryEntity(productId));
        entity.update(result.summary(), reviews.size(), result.generatedBy(), Instant.now());
        return Optional.of(toDto(summaryRepository.save(entity)));
    }

    private static ReviewSummaryDto toDto(ReviewSummaryEntity e) {
        return new ReviewSummaryDto(
                e.getProductId(), e.getSummary(), e.getReviewCount(), e.getGeneratedBy(), e.getGeneratedAt());
    }
}
