package com.david.localnews.backend.infrastructure.news.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class OpenAiClient {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    private final Duration timeout;

    public OpenAiClient(
            WebClient.Builder builder,
            @Value("${openai.apiKey}") String apiKey,
            @Value("${openai.model:gpt-4o-mini}") String model,
            @Value("${openai.timeoutSec:30}") int timeoutSec
    ) {
        this.webClient = builder.baseUrl("https://api.openai.com/v1").build();
        this.apiKey = apiKey;
        this.model = model;
        this.timeout = Duration.ofSeconds(timeoutSec);
    }

    public Mono<String> classify(String systemPrompt, String userPrompt) {
        Map<String, Object> body = Map.of(
                "model", model,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "temperature", 0.2
        );

        return webClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(timeout)
                .map(resp -> {
                    List<?> choices = (List<?>) resp.get("choices");
                    if (choices == null || choices.isEmpty()) return "";
                    Object first = choices.get(0);
                    if (!(first instanceof Map)) return "";
                    Map<?, ?> firstMap = (Map<?, ?>) first;
                    Object msgObj = firstMap.get("message");
                    if (!(msgObj instanceof Map)) return "";
                    Map<?, ?> msg = (Map<?, ?>) msgObj;
                    Object content = msg.get("content");
                    return content != null ? content.toString() : "";
                });
    }

    public String classifySync(String systemPrompt, String userPrompt) {
        return classify(systemPrompt, userPrompt).block(timeout);
    }
}
