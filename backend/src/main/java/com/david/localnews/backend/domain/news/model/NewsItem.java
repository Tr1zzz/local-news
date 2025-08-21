package com.david.localnews.backend.domain.news.model;

import java.time.Instant;

public record NewsItem(
        Integer id,
        Long rawId,
        String title,
        String summary,
        String url,
        String source,
        boolean isLocal,
        Long cityId,
        Integer confidence,
        Instant decidedAt
) {}