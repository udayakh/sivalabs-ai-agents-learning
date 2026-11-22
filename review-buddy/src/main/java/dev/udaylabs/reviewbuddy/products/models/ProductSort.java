package dev.udaylabs.reviewbuddy.products.models;

import java.util.Arrays;

public enum ProductSort {
    NEWEST("newest", "Newest"),
    TOP_RATED("top-rated", "Top rated"),
    MOST_REVIEWED("most-reviewed", "Most reviewed");

    private final String code;
    private final String label;

    ProductSort(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    /** Lenient parsing: unknown / missing values fall back to NEWEST. */
    public static ProductSort fromCode(String code) {
        return Arrays.stream(values())
                .filter(s -> s.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(NEWEST);
    }
}
