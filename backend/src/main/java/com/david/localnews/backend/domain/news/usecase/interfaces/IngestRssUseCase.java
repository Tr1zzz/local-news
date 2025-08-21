package com.david.localnews.backend.domain.news.usecase.interfaces;

public interface IngestRssUseCase {
    int ingestAll(Integer topCities, Integer maxLocal, Integer maxGlobal);
}