package com.david.localnews.backend.domain.news.usecase.implementation;

import com.david.localnews.backend.domain.news.model.NewsItem;
import com.david.localnews.backend.domain.news.port.NewsPort;
import com.david.localnews.backend.domain.news.usecase.interfaces.GetNewsUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class GetNewsUseCaseHandler implements GetNewsUseCase {
    private final NewsPort newsRepo;

    @Override
    public Page<NewsItem> get(Long cityId, String scope, Pageable pageable) {
        if (cityId != null) return newsRepo.findByCityIdOrderByDecidedAtDesc(cityId, pageable);
        return switch (scope == null ? "local" : scope) {
            case "global" -> newsRepo.findByIsLocalOrderByDecidedAtDesc(false, pageable);
            case "all" -> newsRepo.findAllByOrderByDecidedAtDesc(pageable);
            default -> newsRepo.findByIsLocalOrderByDecidedAtDesc(true, pageable);
        };
    }
}