package com.edward.order.repository;

import com.edward.order.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {

    List<CartDetail> findAllByCartId(Long cartId);

    Optional<CartDetail> findByCartIdAndProductId(Long cartId, Long productId);
}
