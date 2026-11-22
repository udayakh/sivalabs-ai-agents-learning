package dev.udaylabs.reviewbuddy.products.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    /*
     * Empty strings (instead of nulls) are used for "no filter" because PostgreSQL cannot infer
     * the type of a null bind parameter in "(:param is null or ...)" expressions.
     */
    @Query(value = """
            select p from ProductEntity p join fetch p.category c
            where (:categorySlug = '' or c.slug = :categorySlug)
              and (:query = ''
                   or lower(p.name) like lower(concat('%', :query, '%'))
                   or lower(p.tagline) like lower(concat('%', :query, '%')))
            """, countQuery = """
            select count(p) from ProductEntity p join p.category c
            where (:categorySlug = '' or c.slug = :categorySlug)
              and (:query = ''
                   or lower(p.name) like lower(concat('%', :query, '%'))
                   or lower(p.tagline) like lower(concat('%', :query, '%')))
            """)
    Page<ProductEntity> search(
            @Param("query") String query, @Param("categorySlug") String categorySlug, Pageable pageable);

    @Query("select p from ProductEntity p join fetch p.category where p.slug = :slug")
    Optional<ProductEntity> findBySlug(@Param("slug") String slug);

    boolean existsBySlug(String slug);
}
