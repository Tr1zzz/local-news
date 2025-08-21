package com.david.localnews.backend.application.news.controller;

import com.david.localnews.backend.domain.news.usecase.interfaces.IngestRssUseCase;
import com.david.localnews.backend.domain.news.usecase.interfaces.NewsStatsUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ingest")
@CrossOrigin
@RequiredArgsConstructor
public class IngestController {

    private final IngestRssUseCase ingest;
    private final NewsStatsUseCase stats;

    @PostMapping("/run")
    public Map<String, Object> run(
            @RequestParam(required = false) Integer topCities,
            @RequestParam(required = false) Integer maxLocal,
            @RequestParam(required = false) Integer maxGlobal) {


        int inserted = ingest.ingestAll(topCities, maxLocal, maxGlobal);
        return Map.of("inserted", inserted, "total", stats.totalRaw());
    }


    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return Map.of(
                "total", stats.totalRaw(),
                "local", stats.countLocalCandidates(),
                "global", stats.countGlobalCandidates(),
                "lastHour", stats.countFetchedLastSeconds(3600)
        );
    }
}