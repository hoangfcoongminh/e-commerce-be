package com.edward.order.repository;

import com.edward.order.entity.Product;
import com.edward.order.entity.PromotionProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Long> {

    List<PromotionProduct> findAllByProductIdIn(List<Long> productIds);

    List<PromotionProduct> findAllByProductId(Long productId);

    List<PromotionProduct> findAllByPromotionId(Long promotionId);

    @Query(value = "SELECT p " +
            "FROM PromotionProduct pp " +
            "JOIN Product p ON p.id = pp.productId " +
            "JOIN Promotion pr ON pr.id = pp.promotionId " +
            "WHERE pr.slug = :slug")
    Page<Product> findAllByPromotionSlug(String slug, Pageable pageable);
}
