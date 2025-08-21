package com.david.localnews.backend.infrastructure.city.adapter;

import com.david.localnews.backend.domain.city.port.CityLookupPort;
import com.david.localnews.backend.infrastructure.city.repository.CityJpaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CityLookupAdapter implements CityLookupPort {
    private final CityJpaRepository cityRepo;

    @Override public Long findExactId(String city, String state) {
        return cityRepo.findExact(city, state).map(com.david.localnews.backend.infrastructure.city.entity.CityEntity::getId).orElse(null);
    }

    @Override public Long findBestIdByName(String city) {
        return cityRepo.findBestByName(city).map(com.david.localnews.backend.infrastructure.city.entity.CityEntity::getId).orElse(null);
    }
}