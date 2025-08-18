package com.david.localnews.backend.city;

public record CityDto(
        Long id,
        String name,
        String stateId,
        String stateName,
        Double lat,
        Double lon,
        Integer population
) {
    public static CityDto from(City c) {
        return new CityDto(c.getId(), c.getName(), c.getStateId(), c.getStateName(),
                c.getLat(), c.getLon(), c.getPopulation());
    }
}