package com.david.localnews.backend.infrastructure.news.repository;

import com.david.localnews.backend.domain.news.enums.RssFeedType;
import com.david.localnews.backend.infrastructure.news.entity.RawNewsEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface RawNewsJpaRepository extends JpaRepository<RawNewsEntity, Long> {
    boolean existsByUrl(String url);
    long countByFeedType(RssFeedType type);
    long countByFetchedAtAfter(Instant after);

    @Query("""
SELECT r FROM RawNewsEntity r
WHERE NOT EXISTS (
SELECT 1 FROM NewsItemEntity n
WHERE n.rawId = r.id
)
ORDER BY r.fetchedAt DESC
""")
    List<RawNewsEntity> findTopNNotInNewsItem(Pageable pageable);
}