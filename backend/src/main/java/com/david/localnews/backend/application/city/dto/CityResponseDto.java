package com.david.localnews.backend.application.city.dto;

public record CityResponseDto(
        Long id,
        String name,
        String stateId,
        String stateName,
        Double lat,
        Double lon,
        Integer population
) {}