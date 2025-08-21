package com.david.localnews.backend.domain.news.model;

import com.david.localnews.backend.domain.news.enums.RssFeedType;

import java.time.Instant;

public record RawNews(
        Long id,
        String title,
        String summary,
        String url,
        String source,
        Instant publishedAt,
        Instant fetchedAt,
        RssFeedType feedType
) {}