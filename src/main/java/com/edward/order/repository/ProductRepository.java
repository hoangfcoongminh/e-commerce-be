package com.edward.order.repository;

import com.edward.order.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllBySubCategoryIdIn(List<Long> ids);

    @Query(value = "SELECT p " +
            "FROM Product p " +
            "WHERE p.status = 1")
    Page<Product> findAllAndActive(Pageable pageable);

    @Query(value = "SELECT p " +
            "FROM Product p " +
            "WHERE (:search IS NULL OR LOWER(p.name) LIKE CONCAT('%', :search, '%') " +
            "OR LOWER(p.description) LIKE CONCAT('%', :search, '%')) " +
            "AND (:subCategoryIds IS NULL OR p.subCategoryId IN :subCategoryIds) " +
            "AND p.status = 1")
    Page<Product> search(
            String search,
            List<Long> subCategoryIds,
            Pageable pageable
    );

    @Query(value = "SELECT COUNT(p) " +
            "FROM Product p " +
            "WHERE p.slug IN :slugs " +
            "AND p.status = 1")
    Long countActiveBySlugs(List<String> slugs);

}
