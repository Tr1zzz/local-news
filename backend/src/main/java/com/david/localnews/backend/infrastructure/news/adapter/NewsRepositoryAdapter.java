package com.david.localnews.backend.infrastructure.news.adapter;

import com.david.localnews.backend.domain.news.model.NewsItem;
import com.david.localnews.backend.domain.news.port.NewsPort;
import com.david.localnews.backend.infrastructure.news.entity.NewsItemEntity;
import com.david.localnews.backend.infrastructure.news.repository.NewsItemJpaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsRepositoryAdapter implements NewsPort {
    private final NewsItemJpaRepository jpa;

    private static NewsItem toDomain(NewsItemEntity e) {
        return new NewsItem(e.getId(), e.getRawId(), e.getTitle(), e.getSummary(), e.getUrl(), e.getSource(),
                e.isLocal(), e.getCityId(), e.getConfidence(), e.getDecidedAt());
    }
    private static NewsItemEntity toEntity(NewsItem d) {
        return NewsItemEntity.builder()
                .id(d.id())
                .rawId(d.rawId())
                .title(d.title())
                .summary(d.summary())
                .url(d.url())
                .source(d.source())
                .isLocal(d.isLocal())
                .cityId(d.cityId())
                .confidence(d.confidence())
                .decidedAt(d.decidedAt())
                .build();
    }

    @Override public boolean existsByRawId(Long rawId) { return jpa.existsByRawId(rawId); }

    @Override public long count() { return jpa.count(); }

    @Override public Page<NewsItem> findByCityIdOrderByDecidedAtDesc(Long cityId, Pageable p) { return jpa.findByCityIdOrderByDecidedAtDesc(cityId, p).map(NewsRepositoryAdapter::toDomain); }

    @Override public Page<NewsItem> findByIsLocalOrderByDecidedAtDesc(boolean isLocal, Pageable p) { return jpa.findByIsLocalOrderByDecidedAtDesc(isLocal, p).map(NewsRepositoryAdapter::toDomain); }

    @Override public Page<NewsItem> findAllByOrderByDecidedAtDesc(Pageable p) { return jpa.findAllByOrderByDecidedAtDesc(p).map(NewsRepositoryAdapter::toDomain); }

    @Override public NewsItem save(NewsItem item) { return toDomain(jpa.save(toEntity(item))); }
}