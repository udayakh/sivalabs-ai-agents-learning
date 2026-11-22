package dev.udaylabs.reviewbuddy.products.models;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProductSortTest {

    @Test
    void parsesKnownCodesIgnoringCase() {
        assertThat(ProductSort.fromCode("TOP-RATED")).isEqualTo(ProductSort.TOP_RATED);
        assertThat(ProductSort.fromCode("most-reviewed")).isEqualTo(ProductSort.MOST_REVIEWED);
    }

    @Test
    void unknownOrMissingCodeFallsBackToNewest() {
        assertThat(ProductSort.fromCode("bogus")).isEqualTo(ProductSort.NEWEST);
        assertThat(ProductSort.fromCode(null)).isEqualTo(ProductSort.NEWEST);
    }

    @Test
    void searchCriteriaNormalizesInput() {
        var criteria = new ProductSearchCriteria(null, " ai ", null, -3);
        assertThat(criteria.query()).isEmpty();
        assertThat(criteria.categorySlug()).isEqualTo("ai");
        assertThat(criteria.sort()).isEqualTo(ProductSort.NEWEST);
        assertThat(criteria.page()).isEqualTo(1);
    }
}
