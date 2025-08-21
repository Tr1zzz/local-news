package com.david.localnews.backend.application.city.dto;

import jakarta.validation.constraints.NotBlank;

public record CityRequestDto(
        @NotBlank String name,
        String stateId,
        String stateName,
        Double lat,
        Double lon,
        Integer population
) {}