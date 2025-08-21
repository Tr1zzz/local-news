package com.david.localnews.backend.application.news.dto;

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
) {}