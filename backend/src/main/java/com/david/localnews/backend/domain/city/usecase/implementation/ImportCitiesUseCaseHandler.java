package com.david.localnews.backend.domain.city.usecase.implementation;

import com.david.localnews.backend.domain.city.port.ImportCitiesPort;
import com.david.localnews.backend.domain.city.usecase.interfaces.ImportCitiesUseCase;

public class ImportCitiesUseCaseHandler implements ImportCitiesUseCase {
    private final ImportCitiesPort importer;

    public ImportCitiesUseCaseHandler(ImportCitiesPort importer) {
        this.importer = importer;
    }

    @Override
    public int importIfEmpty() {
        return importer.importIfEmpty();
    }
}