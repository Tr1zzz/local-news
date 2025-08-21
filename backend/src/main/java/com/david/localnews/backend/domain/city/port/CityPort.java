package com.david.localnews.backend.domain.city.port;

import com.david.localnews.backend.domain.city.model.City;

import java.util.List;

public interface CityPort {
    long count();
    City save(City city);
    List<City> searchPrefix(String q, int limit);
    List<City> topByPopulation(int limit);
}