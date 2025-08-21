package com.david.localnews.backend.domain.news.usecase.interfaces;

public interface ClassifyNewsUseCase {
    int classifyBatch(int limit);
    long totalItems();
}
