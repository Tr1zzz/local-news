package com.david.localnews.backend.application.news.controller;

import com.david.localnews.backend.application.news.dto.NewsItemDto;
import com.david.localnews.backend.application.news.mapper.NewsDtoMapper;
import com.david.localnews.backend.domain.news.usecase.interfaces.ClassifyNewsUseCase;
import com.david.localnews.backend.domain.news.usecase.interfaces.GetNewsUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
@CrossOrigin
@RequiredArgsConstructor
public class NewsController {

    private final GetNewsUseCase getNews;
    private final ClassifyNewsUseCase classify;

    @GetMapping
    public List<NewsItemDto> byCity(
            @RequestParam(required = false) Long cityId,
            @RequestParam(defaultValue = "local") String scope, // local | global | all
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        var pageable = PageRequest.of(Math.max(0, page), Math.min(size, 100));

        scope = scope.toLowerCase(Locale.ROOT);
        return getNews.get(cityId, scope, pageable)
                .map(NewsDtoMapper::toDto)
                .getContent();
    }

    @PostMapping("/classify")
    public Map<String, Object> classify(@RequestParam(defaultValue = "50") int limit) {
        int done = classify.classifyBatch(Math.max(1, Math.min(limit, 200)));
        long totalItems = classify.totalItems();
        return Map.of("classified", done, "totalItems", totalItems);
    }
}