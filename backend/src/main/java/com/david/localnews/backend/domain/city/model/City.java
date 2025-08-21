package com.david.localnews.backend.domain.city.model;

public record City(
        Long id,
        String name,
        String stateId,
        String stateName,
        Double lat,
        Double lon,
        Integer population
) {}
