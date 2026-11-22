package dev.udaylabs.reviewbuddy.shared;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;

/** Framework independent page of data that is safe to hand over to the view layer. */
public record PagedResult<T>(
        List<T> data,
        long totalElements,
        int pageNumber,
        int totalPages,
        boolean isFirst,
        boolean isLast,
        boolean hasNext,
        boolean hasPrevious) {

    public static <S, T> PagedResult<T> of(Page<S> page, Function<S, T> mapper) {
        return new PagedResult<>(
                page.getContent().stream().map(mapper).toList(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious());
    }
}
