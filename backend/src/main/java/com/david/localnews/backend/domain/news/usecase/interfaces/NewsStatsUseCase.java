package com.david.localnews.backend.domain.news.usecase.interfaces;

public interface NewsStatsUseCase {
    long totalRaw();
    long countLocalCandidates();
    long countGlobalCandidates();
    long countFetchedLastSeconds(int seconds);
}