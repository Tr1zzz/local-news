package com.david.localnews.backend.news;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ingest")
@RequiredArgsConstructor
@CrossOrigin
public class IngestController {

    private final RssIngestService ingestService;
    private final RawNewsRepository rawRepo;

    // POST /api/ingest/run?topCities=100&maxLocal=2&maxGlobal=8
    @PostMapping("/run")
    public Map<String, Object> run(
            @RequestParam(required = false) Integer topCities,
            @RequestParam(required = false) Integer maxLocal,
            @RequestParam(required = false) Integer maxGlobal
    ) {
        int inserted = ingestService.ingestAll(topCities, maxLocal, maxGlobal);
        return Map.of(
                "inserted", inserted,
                "total", rawRepo.count()
        );
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return Map.of(
                "total", rawRepo.count(),
                "local", rawRepo.countByFeedType(RssFeedType.LOCAL_CANDIDATE),
                "global", rawRepo.countByFeedType(RssFeedType.GLOBAL_CANDIDATE),
                "lastHour", rawRepo.countByFetchedAtAfter(java.time.Instant.now().minusSeconds(3600))
        );
    }
}
