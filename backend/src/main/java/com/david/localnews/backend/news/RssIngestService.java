package com.david.localnews.backend.news;

import com.david.localnews.backend.city.City;
import com.david.localnews.backend.city.CityRepository;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RssIngestService {

    private final RawNewsRepository rawRepo;
    private final CityRepository cityRepo;

    // ---- CONFIG ----
    @Value("${app.ingest.topCities:100}")
    private int topCities;

    @Value("${app.ingest.delayMsBetweenCalls:200}")
    private long delayMs;

    @Value("${app.ingest.local.maxPerFeed:2}")
    private int localMaxPerFeed;

    @Value("${app.ingest.global.maxPerFeed:8}")
    private int globalMaxPerFeed;

    @Value("${app.ingest.templates.googleCity}")
    private String googleCityTemplate;

    // старые глобальные ленты читаем из YAML
    @Value("#{'${app.rss.global}'.split(',')}")
    private List<String> globalFeeds;

    private static final int CONNECT_TIMEOUT_MS = 6000;
    private static final int READ_TIMEOUT_MS    = 10000;

    /**
     * Основной запуск. Можно вызывать из контроллера.
     * Позволяет переопределить параметры через query-параметры (если null — берем значения из YAML).
     */
    @Transactional
    public int ingestAll(Integer overrideTopCities,
                         Integer overrideLocalMaxPerFeed,
                         Integer overrideGlobalMaxPerFeed) {

        final int citiesToTake = (overrideTopCities != null) ? overrideTopCities : topCities;
        final int localLimit   = (overrideLocalMaxPerFeed != null) ? overrideLocalMaxPerFeed : localMaxPerFeed;
        final int globalLimit  = (overrideGlobalMaxPerFeed != null) ? overrideGlobalMaxPerFeed : globalMaxPerFeed;

        // 1) Сформируем локальные фиды для топ-городов
        List<String> localFeeds = buildCityFeeds(citiesToTake);

        int inserted = 0;
        inserted += ingestGroup(localFeeds, RssFeedType.LOCAL_CANDIDATE, localLimit);
        inserted += ingestGroup(globalFeeds, RssFeedType.GLOBAL_CANDIDATE, globalLimit);

        log.info("Ingest finished. Inserted {} new items. Totals: local={}, global={}",
                inserted,
                rawRepo.countByFeedType(RssFeedType.LOCAL_CANDIDATE),
                rawRepo.countByFeedType(RssFeedType.GLOBAL_CANDIDATE));

        return inserted;
    }

    // старый метод обратно совместим: если кто-то дергает ingestAll(maxPerFeed)
    @Transactional
    public int ingestAll(int maxPerFeed) {
        return ingestAll(null, maxPerFeed, maxPerFeed);
    }

    /** Генерируем Google News RSS URL для каждого города вида "City ST". */
    private List<String> buildCityFeeds(int limit) {
        var page = cityRepo.topByPopulation(PageRequest.of(0, Math.max(1, limit)));
        List<City> cities = page.getContent();
        List<String> urls = new ArrayList<>(cities.size());

        for (City c : cities) {
            String q = (c.getName() + " " + c.getStateId()).trim();
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
                sum += ingestOne(url, type, maxPerFeed);
            } catch (Exception e) {
                log.warn("Skip feed {} due to error: {}", url, e.getMessage());
            }

            // легкий троттлинг против rate-limit
            if (delayMs > 0) {
                try { Thread.sleep(delayMs); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        }
        return sum;
    }

    private int ingestOne(String feedUrl, RssFeedType type, int maxPerFeed) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(feedUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(READ_TIMEOUT_MS);
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124 Safari/537.36");
            conn.setRequestProperty("Accept",
                    "application/rss+xml, application/xml;q=0.9, text/xml;q=0.8, */*;q=0.7");

            int inserted = 0;

            var input = new SyndFeedInput();
            try (XmlReader reader = new XmlReader(conn)) {
                var feed = input.build(reader);

                String source = (feed.getTitle() != null && !feed.getTitle().isBlank())
                        ? feed.getTitle().trim()
                        : url.getHost();

                int taken = 0;
                for (SyndEntry e : feed.getEntries()) {
                    if (taken++ >= maxPerFeed) break;

                    String link = e.getLink() == null ? "" : e.getLink().trim();
                    if (link.isEmpty() || rawRepo.existsByUrl(link)) continue;

                    String title = safe(e.getTitle());
                    if (title.isEmpty()) continue;

                    String summary = extractSummary(e);
                    Instant published = e.getPublishedDate() != null
                            ? e.getPublishedDate().toInstant()
                            : (e.getUpdatedDate() != null ? e.getUpdatedDate().toInstant() : null);

                    rawRepo.save(RawNews.builder()
                            .title(title)
                            .summary(summary)
                            .url(link)
                            .source(source)
                            .publishedAt(published)
                            .fetchedAt(Instant.now())
                            .feedType(type)
                            .build());
                    inserted++;
                }
            }

            log.info("Feed {} -> {} new items (type={})", feedUrl, inserted, type);
            return inserted;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static String extractSummary(SyndEntry e) {
        try {
            String html = null;
            if (e.getDescription() != null && e.getDescription().getValue() != null) {
                html = e.getDescription().getValue();
            } else if (!e.getContents().isEmpty() && e.getContents().get(0).getValue() != null) {
                html = e.getContents().get(0).getValue();
            }
            if (html == null) return "";
            String plain = Jsoup.parse(html).text();
            return plain.length() > 1000 ? plain.substring(0, 1000) : plain;
        } catch (Exception ex) {
            return "";
        }
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }
}
