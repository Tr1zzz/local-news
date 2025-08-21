package com.david.localnews.backend.domain.news.port;

import com.david.localnews.backend.domain.news.enums.RssFeedType;
import com.david.localnews.backend.domain.news.model.RawNews;

import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface RawNewsPort {
    boolean existsByUrl(String url);
    long count();
    long countByFeedType(RssFeedType type);
    long countByFetchedAtAfter(Instant after);
    List<RawNews> findTopNNotInNewsItem(Pageable pageable);
    List<RawNews> findTopNNotInNewsItem(int limit);
    RawNews save(RawNews raw);
}