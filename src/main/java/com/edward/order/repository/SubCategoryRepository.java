package com.edward.order.repository;

import com.edward.order.entity.SubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    List<SubCategory> findAllByCategoryId(Long id);

    @Query(value = "SELECT c " +
            "FROM SubCategory c " +
            "WHERE c.id = :id " +
            "AND c.status = 1")
    Optional<SubCategory> findByIdAndActive(Long id);

    @Query(value = "SELECT c " +
            "FROM SubCategory c " +
            "WHERE c.slug = :slug " +
            "AND c.status = 1")
    Optional<SubCategory> findBySlugAndActive(String slug);

    @Query(value = "SELECT c " +
            "FROM SubCategory c " +
            "WHERE c.status = 1")
    List<SubCategory> findAllAndActive();

    @Query(value = "SELECT c " +
            "FROM SubCategory c " +
            "WHERE (:search IS NULL " +
            "OR :search LIKE CONCAT('%', LOWER(c.name), '%') " +
            "OR :search LIKE CONCAT('%', LOWER(c.description), '%')) " +
            "AND (c.categoryId IN :categoryIds) " +
            "AND (c.status IS NULL OR c.status = :status)")
    Page<SubCategory> search(String search, List<Long> categoryIds, Integer status, Pageable pageable);
}
