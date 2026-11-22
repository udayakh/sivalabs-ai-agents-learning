package dev.udaylabs.reviewbuddy.products.models;

public record ProductSearchCriteria(String query, String categorySlug, ProductSort sort, int page) {

    public static final int PAGE_SIZE = 9;

    public ProductSearchCriteria {
        query = query == null ? "" : query.trim();
        categorySlug = categorySlug == null ? "" : categorySlug.trim();
        sort = sort == null ? ProductSort.NEWEST : sort;
        page = Math.max(page, 1);
    }
}
