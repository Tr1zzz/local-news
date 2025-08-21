package com.david.localnews.backend.domain.news.port;

import com.david.localnews.backend.domain.news.model.NewsItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsPort {
    boolean existsByRawId(Long rawId);
    long count();
    Page<NewsItem> findByCityIdOrderByDecidedAtDesc(Long cityId, Pageable pageable);
    Page<NewsItem> findByIsLocalOrderByDecidedAtDesc(boolean isLocal, Pageable pageable);
    Page<NewsItem> findAllByOrderByDecidedAtDesc(Pageable pageable);
    NewsItem save(NewsItem item);
}