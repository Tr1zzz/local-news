package com.david.localnews.backend.domain.news.usecase.implementation;

import com.david.localnews.backend.domain.city.port.CityLookupPort;
import com.david.localnews.backend.domain.news.model.NewsItem;
import com.david.localnews.backend.domain.news.model.RawNews;
import com.david.localnews.backend.domain.news.port.NewsClassifierPort;
import com.david.localnews.backend.domain.news.port.NewsPort;
import com.david.localnews.backend.domain.news.port.RawNewsPort;
import com.david.localnews.backend.domain.news.usecase.interfaces.ClassifyNewsUseCase;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class ClassifyNewsUseCaseHandler implements ClassifyNewsUseCase {
    private final NewsClassifierPort classifier;
    private final RawNewsPort rawRepo;
    private final NewsPort newsRepo;
    private final CityLookupPort cityLookup;

    @Override
    public int classifyBatch(int limit) {
        List<RawNews> raws = rawRepo.findTopNNotInNewsItem(Math.max(1, Math.min(limit, 200)));
        int inserted = 0;
        for (RawNews r : raws) {
            if (r == null) continue;
            if (newsRepo.existsByRawId(r.id())) continue; // safety check, как в исходнике

            var res = classifier.classify(safe(r.title()), safe(r.summary()), safe(r.source()));
            Long cityId = null;
            if (res.isLocal()) {
                String city = safe(res.city());
                String st = safe(res.state());
                if (!city.isEmpty() && st.length() == 2) cityId = cityLookup.findExactId(city, st);
                if (cityId == null && !city.isEmpty()) cityId = cityLookup.findBestIdByName(city);
            }

            var ni = new NewsItem(
                    null, r.id(), r.title(), r.summary(), r.url(), r.source(),
                    res.isLocal(), cityId, Math.max(0, Math.min(res.confidence(), 100)),
                    Instant.now()
            );
            newsRepo.save(ni);
            inserted++;
        }
        return inserted;
    }

    @Override
    public long totalItems() { return newsRepo.count(); }

    private static String safe(String s) { return s == null ? "" : s.trim(); }
}