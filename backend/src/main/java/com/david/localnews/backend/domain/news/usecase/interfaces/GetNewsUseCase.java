package com.david.localnews.backend.domain.news.usecase.interfaces;

import com.david.localnews.backend.domain.news.model.NewsItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetNewsUseCase {
    Page<NewsItem> get(Long cityId, String scope, Pageable pageable);
}