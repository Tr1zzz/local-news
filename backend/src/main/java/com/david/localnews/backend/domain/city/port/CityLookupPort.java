package com.david.localnews.backend.domain.city.port;

public interface CityLookupPort {
    Long findExactId(String city, String state);
    Long findBestIdByName(String city);
}