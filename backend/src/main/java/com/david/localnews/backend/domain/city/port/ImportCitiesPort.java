package com.david.localnews.backend.domain.city.port;

public interface ImportCitiesPort {
    /**
     * Imports the city file from the default source (infra) only if the table is empty.
     * @return number of inserted records
     */
    int importIfEmpty();
}