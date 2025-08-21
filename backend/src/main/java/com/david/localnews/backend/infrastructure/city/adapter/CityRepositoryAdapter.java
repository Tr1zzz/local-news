package com.david.localnews.backend.infrastructure.city.adapter;

import com.david.localnews.backend.domain.city.model.City;
import com.david.localnews.backend.domain.city.port.CityPort;
import com.david.localnews.backend.infrastructure.city.entity.CityEntity;
import com.david.localnews.backend.infrastructure.city.repository.CityJpaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CityRepositoryAdapter implements CityPort {

    private final CityJpaRepository jpa;

    private static City toDomain(CityEntity e) {
        return new City(e.getId(), e.getName(), e.getStateId(), e.getStateName(), e.getLat(), e.getLon(), e.getPopulation());
    }

    private static CityEntity toEntity(City c) {
        CityEntity e = new CityEntity();
        e.setId(c.id());
        e.setName(c.name());
        e.setStateId(c.stateId());
        e.setStateName(c.stateName());
        e.setLat(c.lat());
        e.setLon(c.lon());
        e.setPopulation(c.population());
        return e;
    }

    @Override
    public long count() { return jpa.count(); }

    @Override
    public City save(City city) { return toDomain(jpa.save(toEntity(city))); }

    @Override
    public List<City> searchPrefix(String q, int limit) {
        int clamped = Math.max(1, Math.min(limit, 25));
        return jpa.searchPrefix(q, PageRequest.of(0, clamped))
                .map(CityRepositoryAdapter::toDomain)
                .getContent();
    }

    @Override
    public List<City> topByPopulation(int limit) {
        int clamped = Math.max(1, limit);
        return jpa.topByPopulation(PageRequest.of(0, clamped))
                .map(CityRepositoryAdapter::toDomain)
                .getContent();
    }
}