package com.david.localnews.backend.domain.news.usecase.implementation;

import com.david.localnews.backend.domain.city.port.CityPort;
import com.david.localnews.backend.domain.news.enums.RssFeedType;
import com.david.localnews.backend.domain.news.model.RawNews;
import com.david.localnews.backend.domain.news.port.RawNewsPort;
import com.david.localnews.backend.domain.news.port.RssClientPort;
import com.david.localnews.backend.domain.news.usecase.interfaces.IngestRssUseCase;
import com.david.localnews.backend.domain.news.usecase.interfaces.NewsStatsUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class IngestRssUseCaseHandler implements IngestRssUseCase, NewsStatsUseCase {

    private final RssClientPort rss;
    private final RawNewsPort rawRepo;
    private final CityPort cityPort;
    private final int topCities;
    private final long delayMs;
    private final int localMaxPerFeed;
    private final int globalMaxPerFeed;
    private final String googleCityTemplate;
    private final List<String> globalFeeds;

    @Override
    public int ingestAll(Integer overrideTopCities, Integer overrideLocalMax, Integer overrideGlobalMax) {
        final int citiesToTake = (overrideTopCities != null) ? overrideTopCities : topCities;
        final int localLimit   = (overrideLocalMax != null) ? overrideLocalMax : localMaxPerFeed;
        final int globalLimit  = (overrideGlobalMax != null) ? overrideGlobalMax : globalMaxPerFeed;

        List<String> localFeeds = buildCityFeeds(citiesToTake);
        int inserted = 0;
        inserted += ingestGroup(localFeeds, RssFeedType.LOCAL_CANDIDATE, localLimit);
        inserted += ingestGroup(globalFeeds, RssFeedType.GLOBAL_CANDIDATE, globalLimit);

        log.info("Ingest finished. Inserted {} new items.", inserted);
        return inserted;
    }

    private List<String> buildCityFeeds(int limit) {
        var cities = cityPort.topByPopulation(limit);
        List<String> urls = new ArrayList<>(cities.size());
        for (var c : cities) {
            String q = (c.name() + " " + c.stateId()).trim();
            String encoded = URLEncoder.encode(q, StandardCharsets.UTF_8);
            urls.add(googleCityTemplate.formatted(encoded));
        }
        log.info("Prepared {} city feeds via Google News template.", urls.size());
        return urls;
    }

    private int ingestGroup(List<String> feeds, RssFeedType type, int maxPerFeed) {
        if (feeds == null || feeds.isEmpty()) return 0;
        int sum = 0;
        for (String raw : feeds) {
            String url = raw == null ? "" : raw.trim();
            if (url.isEmpty()) continue;

            try {
                List<RawNews> list = rss.fetchOne(url, type, maxPerFeed);
                sum += list.size();
            } catch (Exception e) {
                log.warn("Skip feed {} due to error: {}", url, e.getMessage());
            }

            if (delayMs > 0) {
                try { Thread.sleep(delayMs); }
                catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        }
        return sum;
    }

    // --- Stats ---
    @Override public long totalRaw() { return rawRepo.count(); }
    @Override public long countLocalCandidates() { return rawRepo.countByFeedType(RssFeedType.LOCAL_CANDIDATE); }
    @Override public long countGlobalCandidates() { return rawRepo.countByFeedType(RssFeedType.GLOBAL_CANDIDATE); }
    @Override public long countFetchedLastSeconds(int seconds) {
        return rawRepo.countByFetchedAtAfter(java.time.Instant.now().minusSeconds(seconds));
    }
}