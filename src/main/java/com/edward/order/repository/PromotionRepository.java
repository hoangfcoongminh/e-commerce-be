package com.edward.order.repository;

import com.edward.order.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query(value = "SELECT p " +
            "FROM Promotion p " +
            "WHERE p.id IN :promotionIds " +
            "AND p.status = 1")
    List<Promotion> findAllByIdIn(List<Long> id);

    @Query(value = "SELECT p " +
            "FROM Promotion p " +
            "WHERE p.id IN :promotionIds " +
            "AND p.status = 1")
    Long countActiveByIds(List<Long> promotionIds);
}
