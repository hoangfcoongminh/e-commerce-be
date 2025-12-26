package com.edward.order.repository;

public interface SlugRepository {
    boolean existsBySlug(String slug);
}
