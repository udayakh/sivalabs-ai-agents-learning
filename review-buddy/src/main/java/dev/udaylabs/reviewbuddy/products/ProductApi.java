package dev.udaylabs.reviewbuddy.products;

import dev.udaylabs.reviewbuddy.products.domain.ProductService;
import dev.udaylabs.reviewbuddy.products.models.ProductDto;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Public API of the products module for other modules (e.g. reviews). */
@Component
public class ProductApi {

    private final ProductService productService;

    ProductApi(ProductService productService) {
        this.productService = productService;
    }

    public Optional<ProductDto> findBySlug(String slug) {
        return productService.findBySlug(slug);
    }

    public ProductDto getById(Long productId) {
        return productService.getById(productId);
    }

    public void updateRatingStats(Long productId, double averageRating, int reviewCount) {
        productService.updateRatingStats(productId, averageRating, reviewCount);
    }
}
