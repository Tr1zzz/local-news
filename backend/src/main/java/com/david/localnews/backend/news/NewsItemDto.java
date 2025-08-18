package com.david.localnews.backend.news;

import java.time.Instant;

public record NewsItemDto(
        Integer id,
        String title,
        String summary,
        String url,
        String source,
        boolean isLocal,
        Long cityId,
        Integer confidence,
        Instant decidedAt
) {
    public static NewsItemDto from(NewsItem e) {
        return new NewsItemDto(
                e.getId(), e.getTitle(), e.getSummary(), e.getUrl(), e.getSource(),
                e.isLocal(), e.getCityId(), e.getConfidence(), e.getDecidedAt()
        );
    }
}