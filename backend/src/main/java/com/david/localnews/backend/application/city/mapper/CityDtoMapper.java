package com.david.localnews.backend.application.city.mapper;

import com.david.localnews.backend.application.city.dto.CityRequestDto;
import com.david.localnews.backend.application.city.dto.CityResponseDto;
import com.david.localnews.backend.domain.city.model.City;

public final class CityDtoMapper {
    private CityDtoMapper() {}


    public static City toDomain(CityRequestDto dto) {
        return new City(null, dto.name(), dto.stateId(), dto.stateName(), dto.lat(), dto.lon(), dto.population());
    }


    public static CityResponseDto toResponse(City c) {
        return new CityResponseDto(c.id(), c.name(), c.stateId(), c.stateName(), c.lat(), c.lon(), c.population());
    }
}