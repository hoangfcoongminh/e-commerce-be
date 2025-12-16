package com.edward.order.repository;

import com.edward.order.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    @Query(value = "SELECT c " +
            "FROM Category c " +
            "WHERE c.id = :id " +
            "AND c.status = 1")
    Optional<Category> findByIdAndActive(Long id);

    @Query(value = "SELECT c " +
            "FROM Category c " +
            "WHERE c.slug = :slug " +
            "AND c.status = 1")
    boolean existsBySlugAndActive(String slug);

    @Query(value = "SELECT c " +
            "FROM Category c " +
            "WHERE c.status = 1")
    List<Category> findAllAndActive();

    @Query(value = "SELECT c " +
            "FROM Category c " +
            "WHERE (:search IS NULL " +
            "OR :search LIKE CONCAT('%', LOWER(c.name), '%') " +
            "OR :search LIKE CONCAT('%', LOWER(c.description), '%')) " +
            "AND (c.status IS NULL OR c.status = :status)")
    Page<Category> search(String search, Integer status, Pageable pageable);
}
