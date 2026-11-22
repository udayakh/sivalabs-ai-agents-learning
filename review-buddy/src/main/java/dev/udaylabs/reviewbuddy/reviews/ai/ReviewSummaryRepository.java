package dev.udaylabs.reviewbuddy.reviews.ai;

import org.springframework.data.jpa.repository.JpaRepository;

interface ReviewSummaryRepository extends JpaRepository<ReviewSummaryEntity, Long> {}
