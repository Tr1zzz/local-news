package com.david.localnews.backend.city;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@CrossOrigin // временно, пока не настроим прокси/CloudFront
@RequiredArgsConstructor
public class CityController {

    private final CityRepository cityRepository;

    @GetMapping
    public List<CityDto> search(@RequestParam("query") String query,
                                @RequestParam(value = "limit", defaultValue = "10") int limit) {
        String q = query == null ? "" : query.trim().toLowerCase();
        if (q.isEmpty()) return List.of();
        return cityRepository.searchPrefix(q, PageRequest.of(0, Math.max(1, Math.min(limit, 25))))
                .map(CityDto::from)
                .getContent();
    }
}