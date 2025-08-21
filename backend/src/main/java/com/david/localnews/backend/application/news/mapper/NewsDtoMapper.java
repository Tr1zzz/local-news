package com.david.localnews.backend.application.news.mapper;

import com.david.localnews.backend.application.news.dto.NewsItemDto;
import com.david.localnews.backend.domain.news.model.NewsItem;

public final class NewsDtoMapper {
    private NewsDtoMapper() {}

    public static NewsItemDto toDto(NewsItem e) {
        return new NewsItemDto(e.id(), e.title(), e.summary(), e.url(), e.source(),
                e.isLocal(), e.cityId(), e.confidence(), e.decidedAt());
    }
}