package com.edward.order.repository;

import com.edward.order.entity.PromotionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Long> {

    List<PromotionProduct> findAllByProductIdIn(List<Long> productIds);

    List<PromotionProduct> findAllByProductId(Long productId);
}
