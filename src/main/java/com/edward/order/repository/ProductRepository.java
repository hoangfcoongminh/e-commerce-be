package com.edward.order.repository;

import com.edward.order.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, SlugRepository {

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

    boolean existsBySlugInAndStatus(List<String> slugs, Integer status);

    @Query(value = "SELECT p " +
            "FROM Product p " +
            "WHERE p.id = :id " +
            "AND p.status = 1")
    Optional<Product> findByIdAndActive(Long id);

    List<Product> findAllByIdIn(List<Long> ids);

    @Query(value = "SELECT p " +
            "FROM Product p " +
            "WHERE (:subCategoryIds IS NULL OR p.subCategoryId IN :subCategoryIds) " +
            "AND (:keyword IS NULL OR LOWER(p.name) LIKE CONCAT('%', :keyword, '%') " +
            "OR LOWER(p.description) LIKE CONCAT('%', :keyword, '%')) " +
            "AND p.originalPrice BETWEEN :minPrice AND :maxPrice " +
            "AND p.status = 1")
    Page<Product> filterProducts(
            List<Long> subCategoryIds,
            String keyword,
            Long minPrice,
            Long maxPrice,
            Pageable pageable
    );
}
