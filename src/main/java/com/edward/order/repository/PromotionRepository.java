package com.edward.order.repository;

import com.edward.order.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long>, SlugRepository {

    @Query(value = "SELECT p " +
            "FROM Promotion p " +
            "WHERE p.id IN :promotionIds " +
            "AND p.status = 1")
    List<Promotion> findAllByIdIn(List<Long> promotionIds);

    @Query(value = "SELECT p " +
            "FROM Promotion p " +
            "WHERE p.id IN :promotionIds " +
            "AND p.status = 1")
    Long countActiveByIds(List<Long> promotionIds);

    boolean existsByIdInAndStatus(List<Long> ids, Integer status);

    Optional<Promotion> findBySlug(String slug);
}
