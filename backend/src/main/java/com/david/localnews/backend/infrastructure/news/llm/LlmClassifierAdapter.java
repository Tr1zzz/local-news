package com.david.localnews.backend.infrastructure.news.llm;

import com.david.localnews.backend.domain.news.port.NewsClassifierPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class LlmClassifierAdapter implements NewsClassifierPort {

    private final OpenAiClient openAi;

    private static final String SYS = """
        You classify news for U.S. localities.
        Return STRICT JSON: {"kind":"local|global","city":"<city or empty>","state":"<US state code 2 letters or empty>","confidence":0-100}
        """;

    @Override
    public Classification classify(String title, String summary, String source) {
        String user = """
            TITLE: %s
            SOURCE: %s
            SUMMARY: %s
            Decide local/global for a U.S. city. If local, provide city and state code.
            """.formatted(safe(title), safe(source), safe(summary));

        try {
            String json = openAi.classify(SYS, user)
                    .onErrorResume(err -> {
                        log.warn("LLM failed: {}", err.toString());
                        return Mono.just("{\"kind\":\"global\",\"city\":\"\",\"state\":\"\",\"confidence\":0}");
                    })
                    .block(Duration.ofSeconds(35));

            LlmResult res = LlmParser.parse(json); // <-- статический вызов
            boolean isLocal = res.isLocal();
            int conf = Math.max(0, Math.min(res.confidence(), 100));
            return new Classification(
                    isLocal,
                    safe(res.city()),
                    safe(res.state()).toUpperCase(Locale.US),
                    conf
            );
        } catch (Exception e) {
            return new Classification(false, "", "", 0);
        }
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }
}