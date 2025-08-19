package com.david.localnews.backend.news;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface RawNewsRepository extends JpaRepository<RawNews, Long> {

    boolean existsByUrl(String url);

    long countByFeedType(RssFeedType type);

    long countByFetchedAtAfter(Instant after);

    @Query("""
        SELECT r FROM RawNews r
        WHERE NOT EXISTS (
            SELECT 1 FROM NewsItem n
            WHERE n.rawId = r.id
        )
        ORDER BY r.fetchedAt DESC
        """)
    List<RawNews> findTopNNotInNewsItem(Pageable pageable);

    default List<RawNews> findTopNNotInNewsItem(int limit) {
        return findTopNNotInNewsItem(
                org.springframework.data.domain.PageRequest.of(0, Math.max(1, limit))
        );
    }
}
