package dev.udaylabs.reviewbuddy.products.domain;

import dev.udaylabs.reviewbuddy.products.models.CategoryDto;
import dev.udaylabs.reviewbuddy.products.models.CreateProductCmd;
import dev.udaylabs.reviewbuddy.products.models.ProductDto;
import dev.udaylabs.reviewbuddy.products.models.ProductSearchCriteria;
import dev.udaylabs.reviewbuddy.products.models.ProductSort;
import dev.udaylabs.reviewbuddy.shared.BadRequestException;
import dev.udaylabs.reviewbuddy.shared.PagedResult;
import dev.udaylabs.reviewbuddy.shared.ResourceNotFoundException;
import dev.udaylabs.reviewbuddy.shared.Slugs;
import dev.udaylabs.reviewbuddy.users.UserApi;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    /** Slugs that collide with fixed URLs such as /products/new. */
    private static final Set<String> RESERVED_SLUGS = Set.of("new");

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserApi userApi;

    ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, UserApi userApi) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userApi = userApi;
    }

    public PagedResult<ProductDto> searchProducts(ProductSearchCriteria criteria) {
        var pageable = PageRequest.of(criteria.page() - 1, ProductSearchCriteria.PAGE_SIZE, toSort(criteria.sort()));
        var page = productRepository.search(criteria.query(), criteria.categorySlug(), pageable);
        Map<Long, String> names = userApi.getUserNames(
                page.getContent().stream().map(ProductEntity::getSubmittedBy).collect(Collectors.toSet()));
        return PagedResult.of(page, p -> ProductMapper.toDto(p, names.get(p.getSubmittedBy())));
    }

    public Optional<ProductDto> findBySlug(String slug) {
        return productRepository.findBySlug(slug).map(this::toDtoWithSubmitter);
    }

    public ProductDto getById(Long id) {
        return productRepository
                .findById(id)
                .map(this::toDtoWithSubmitter)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    public List<CategoryDto> getCategories() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(ProductMapper::toDto)
                .toList();
    }

    @Transactional
    public ProductDto createProduct(CreateProductCmd cmd) {
        CategoryEntity category = categoryRepository
                .findById(cmd.categoryId())
                .orElseThrow(() -> new BadRequestException("Invalid category: " + cmd.categoryId()));
        var product = new ProductEntity(
                cmd.name().trim(),
                uniqueSlug(cmd.name()),
                cmd.tagline().trim(),
                cmd.description().trim(),
                cmd.websiteUrl().trim(),
                blankToNull(cmd.imageUrl()),
                category,
                cmd.submittedBy());
        return toDtoWithSubmitter(productRepository.save(product));
    }

    @Transactional
    public void updateRatingStats(Long productId, double averageRating, int reviewCount) {
        ProductEntity product = productRepository
                .findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        product.updateRatingStats(averageRating, reviewCount);
    }

    private ProductDto toDtoWithSubmitter(ProductEntity product) {
        String name = userApi.getUserNames(List.of(product.getSubmittedBy())).get(product.getSubmittedBy());
        return ProductMapper.toDto(product, name);
    }

    private String uniqueSlug(String name) {
        String base = Slugs.slugify(name);
        if (base.isEmpty() || RESERVED_SLUGS.contains(base)) {
            base = "product-" + (base.isEmpty() ? "x" : base);
        }
        String slug = base;
        int counter = 2;
        while (productRepository.existsBySlug(slug)) {
            slug = base + "-" + counter++;
        }
        return slug;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static Sort toSort(ProductSort sort) {
        return switch (sort) {
            case TOP_RATED -> Sort.by(Sort.Order.desc("averageRating"), Sort.Order.desc("reviewCount"));
            case MOST_REVIEWED -> Sort.by(Sort.Order.desc("reviewCount"), Sort.Order.desc("averageRating"));
            case NEWEST -> Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
        };
    }
}
