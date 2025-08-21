package com.david.localnews.backend.domain.city.usecase.implementation;

import com.david.localnews.backend.domain.city.model.City;
import com.david.localnews.backend.domain.city.port.CityPort;
import com.david.localnews.backend.domain.city.usecase.interfaces.SearchCitiesUseCase;

import java.util.List;

public class SearchCitiesUseCaseHandler implements SearchCitiesUseCase {
    private final CityPort repo;

    public SearchCitiesUseCaseHandler(CityPort repo) {
        this.repo = repo;
    }

    @Override
    public List<City> searchPrefix(String q, int limit) {
        return repo.searchPrefix(q, limit);
    }
}