package com.david.localnews.backend.infrastructure.news.adapter;

import com.david.localnews.backend.domain.news.enums.RssFeedType;
import com.david.localnews.backend.domain.news.model.RawNews;
import com.david.localnews.backend.domain.news.port.RawNewsPort;
import com.david.localnews.backend.domain.news.port.RssClientPort;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RssIngestAdapter implements RssClientPort {

    private final RawNewsPort rawRepo;
    private final com.david.localnews.backend.infrastructure.city.repository.CityJpaRepository cityRepo;

    @Value("${app.ingest.delayMsBetweenCalls:200}")
    private long delayMs;

    private static final int CONNECT_TIMEOUT_MS = 6000;
    private static final int READ_TIMEOUT_MS = 10000;

    @Override
    public List<RawNews> fetchOne(String feedUrl, RssFeedType type, int maxPerFeed) {
        List<RawNews> list = new ArrayList<>();
        try {
            ingestOne(feedUrl, type, maxPerFeed, list);
        } catch (Exception e) {
            log.warn("Skip feed {} due to error: {}", feedUrl, e.getMessage());
        }
        return list;
    }

    // --- helpers ---
    private int ingestOne(String feedUrl, RssFeedType type, int maxPerFeed, List<RawNews> out) throws Exception {
        HttpURLConnection conn = null;
        int inserted = 0;
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
                    java.time.Instant published = e.getPublishedDate() != null
                            ? e.getPublishedDate().toInstant()
                            : (e.getUpdatedDate() != null ? e.getUpdatedDate().toInstant() : null);

                    var raw = new RawNews(null, title, summary, link, source, published, java.time.Instant.now(), type);
                    rawRepo.save(raw);
                    if (out != null) out.add(raw);
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
            String plain = org.jsoup.Jsoup.parse(html).text();
            return plain.length() > 1000 ? plain.substring(0, 1000) : plain;
        } catch (Exception ex) {
            return "";
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}