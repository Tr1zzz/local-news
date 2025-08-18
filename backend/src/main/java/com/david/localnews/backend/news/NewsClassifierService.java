package com.david.localnews.backend.news;

import com.david.localnews.backend.city.City;
import com.david.localnews.backend.city.CityRepository;
import com.david.localnews.backend.llm.LlmParser;
import com.david.localnews.backend.llm.LlmResult;
import com.david.localnews.backend.llm.OpenAiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsClassifierService {

    private final RawNewsRepository rawRepo;
    private final NewsItemRepository newsRepo;
    private final CityRepository cityRepo;
    private final OpenAiClient openAi;

    private static final String SYS = """
        You classify news for U.S. localities.
        Return STRICT JSON: {"kind":"local|global","city":"<city or empty>","state":"<US state code 2 letters or empty>","confidence":0-100}
        "local" means primarily relevant to a specific city or metro area; otherwise "global".
        If local: fill city and 2-letter state (e.g., "NY", "CA"). If unsure, leave empty and lower confidence.
        """;

    @Transactional
    public int classifyBatch(int limit) {
        List<RawNews> raws = rawRepo.findTopNNotInNewsItem(limit);
        int inserted = 0;

        for (RawNews r : raws) {
            try {
                if (newsRepo.existsByRawId(r.getId())) continue;

                String user = buildUserPrompt(r);

                String json = openAi.classify(SYS, user)
                        .onErrorResume(err -> {
                            log.warn("LLM failed for {}: {}", r.getUrl(), err.toString());
                            return Mono.just("{\"kind\":\"global\",\"city\":\"\",\"state\":\"\",\"confidence\":0}");
                        })
                        .block(Duration.ofSeconds(35));

                LlmResult res = LlmParser.parse(json);
                boolean isLocal = res.isLocal();
                Long cityId = resolveCityId(res); // << Long

                int conf = Math.max(0, Math.min(res.confidence(), 100));

                NewsItem ni = NewsItem.builder()
                        .rawId(r.getId())
                        .title(r.getTitle())
                        .summary(r.getSummary())
                        .url(r.getUrl())
                        .source(r.getSource())
                        .isLocal(isLocal)
                        .cityId(cityId)    // << Long
                        .confidence(conf)
                        .build();

                newsRepo.save(ni);
                inserted++;
            } catch (Exception e) {
                log.warn("Skip raw {} due to {}", r.getId(), e.getMessage());
            }
        }
        return inserted;
    }

    private static String buildUserPrompt(RawNews r) {
        String title = safe(r.getTitle());
        String summary = safe(r.getSummary());
        String src = safe(r.getSource());
        return """
            TITLE: %s
            SOURCE: %s
            SUMMARY: %s
            Decide local/global for a U.S. city. If local, provide city and state code.
            """.formatted(title, src, summary);
    }

    // теперь возвращаем Long
    private Long resolveCityId(LlmResult res) {
        if (!res.isLocal()) return null;
        String city = safe(res.city());
        String st = safe(res.state()).toUpperCase(Locale.US);

        try {
            if (!city.isEmpty() && st.length() == 2) {
                return cityRepo.findExact(city, st).map(City::getId).orElse(null);
            }
            if (!city.isEmpty()) {
                return cityRepo.findBestByName(city).map(City::getId).orElse(null);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }
}