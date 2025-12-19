package com.edward.order.repository;

import com.edward.order.entity.PromotionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Long> {

    @Query(value = "SELECT p " +
            "FROM PromotionProduct p " +
            "WHERE p.productId IN :productIds " +
            "AND p.status = 1")
    List<PromotionProduct> findAllByProductIds(List<Long> productIds);
}
