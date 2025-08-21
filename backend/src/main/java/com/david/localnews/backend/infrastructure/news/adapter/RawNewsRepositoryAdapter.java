package com.david.localnews.backend.infrastructure.news.adapter;

import com.david.localnews.backend.domain.news.enums.RssFeedType;
import com.david.localnews.backend.domain.news.model.RawNews;
import com.david.localnews.backend.domain.news.port.RawNewsPort;
import com.david.localnews.backend.infrastructure.news.entity.RawNewsEntity;
import com.david.localnews.backend.infrastructure.news.repository.RawNewsJpaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RawNewsRepositoryAdapter implements RawNewsPort {
    private final RawNewsJpaRepository jpa;

    private static RawNews toDomain(RawNewsEntity e) {
        return new RawNews(e.getId(), e.getTitle(), e.getSummary(), e.getUrl(), e.getSource(),
                e.getPublishedAt(), e.getFetchedAt(), e.getFeedType());
    }
    private static RawNewsEntity toEntity(RawNews d) {
        return RawNewsEntity.builder()
                .id(d.id())
                .title(d.title())
                .summary(d.summary())
                .url(d.url())
                .source(d.source())
                .publishedAt(d.publishedAt())
                .fetchedAt(d.fetchedAt())
                .feedType(d.feedType())
                .build();
    }

    @Override public boolean existsByUrl(String url) { return jpa.existsByUrl(url); }

    @Override public long count() { return jpa.count(); }

    @Override public long countByFeedType(RssFeedType type) { return jpa.countByFeedType(type); }

    @Override public long countByFetchedAtAfter(Instant after) { return jpa.countByFetchedAtAfter(after); }

    @Override public List<RawNews> findTopNNotInNewsItem(org.springframework.data.domain.Pageable pageable) {
        return jpa.findTopNNotInNewsItem(pageable).stream().map(RawNewsRepositoryAdapter::toDomain).toList();
    }

    @Override public List<RawNews> findTopNNotInNewsItem(int limit) {
        return findTopNNotInNewsItem(PageRequest.of(0, Math.max(1, limit)));
    }

    @Override public RawNews save(RawNews raw) { return toDomain(jpa.save(toEntity(raw))); }
}