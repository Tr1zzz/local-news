package com.david.localnews.backend.infrastructure.news.repository;

import com.david.localnews.backend.infrastructure.news.entity.NewsItemEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsItemJpaRepository extends JpaRepository<NewsItemEntity, Integer> {
    boolean existsByRawId(Long rawId);
    Page<NewsItemEntity> findByCityIdOrderByDecidedAtDesc(Long cityId, Pageable pageable);
    Page<NewsItemEntity> findByIsLocalOrderByDecidedAtDesc(boolean isLocal, Pageable pageable);
    Page<NewsItemEntity> findAllByOrderByDecidedAtDesc(Pageable pageable);
}