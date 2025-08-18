package com.david.localnews.backend.news;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@CrossOrigin
public class NewsController {

    private final NewsItemRepository newsRepo;
    private final NewsClassifierService classifierService;

    @GetMapping
    public List<NewsItemDto> byCity(
            @RequestParam(required = false) Long cityId,
            @RequestParam(defaultValue = "local") String scope, // local | global | all
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageable = PageRequest.of(Math.max(0, page), Math.min(size, 100));

        if (cityId != null) {
            // если выбран город — показываем новости этого города (они по определению «local»)
            return newsRepo.findByCityIdOrderByDecidedAtDesc(cityId, pageable)
                    .map(NewsItemDto::from).getContent();
        }

        scope = scope.toLowerCase(Locale.ROOT);
        return switch (scope) {
            case "global" -> newsRepo.findByIsLocalOrderByDecidedAtDesc(false, pageable)
                    .map(NewsItemDto::from).getContent();
            case "all" -> newsRepo.findAllByOrderByDecidedAtDesc(pageable)
                    .map(NewsItemDto::from).getContent();
            default -> newsRepo.findByIsLocalOrderByDecidedAtDesc(true, pageable)
                    .map(NewsItemDto::from).getContent();
        };
    }

    @PostMapping("/classify")
    public Map<String, Object> classify(@RequestParam(defaultValue = "50") int limit) {
        int done = classifierService.classifyBatch(Math.max(1, Math.min(limit, 200)));
        long totalItems = newsRepo.count();
        return Map.of("classified", done, "totalItems", totalItems);
    }
}
