package com.david.localnews.backend.domain.city.usecase.interfaces;

import com.david.localnews.backend.domain.city.model.City;

import java.util.List;

public interface SearchCitiesUseCase {
    List<City> searchPrefix(String q, int limit);
}