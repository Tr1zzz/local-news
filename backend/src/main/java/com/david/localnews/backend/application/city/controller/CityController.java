package com.david.localnews.backend.application.city.controller;

import com.david.localnews.backend.application.city.dto.CityResponseDto;
import com.david.localnews.backend.application.city.mapper.CityDtoMapper;
import com.david.localnews.backend.domain.city.usecase.interfaces.SearchCitiesUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@CrossOrigin
@RequiredArgsConstructor
public class CityController {


    private final SearchCitiesUseCase searchCities;


    @GetMapping
    public List<CityResponseDto> search(@RequestParam("query") String query,
                                        @RequestParam(value = "limit", defaultValue = "10") int limit) {
        String q = query == null ? "" : query.trim().toLowerCase();
        if (q.isEmpty()) return List.of();
        int clamped = Math.max(1, Math.min(limit, 25));
        return searchCities.searchPrefix(q, clamped)
                .stream()
                .map(CityDtoMapper::toResponse)
                .toList();
    }
}