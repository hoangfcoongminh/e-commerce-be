package com.edward.order.repository;

import com.edward.order.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllBySubCategoryIdIn(List<Long> ids);

}
