package dev.udaylabs.reviewbuddy.products.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findAllByOrderByNameAsc();
}
