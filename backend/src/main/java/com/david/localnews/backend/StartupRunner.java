package com.david.localnews.backend;

import com.david.localnews.backend.domain.city.usecase.interfaces.ImportCitiesUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {

    private final ImportCitiesUseCase importCitiesUseCase;

    @Override
    public void run(String... args) {
        int imported = importCitiesUseCase.importIfEmpty();
        if (imported > 0) {
            log.info("Imported {} cities on startup", imported);
        } else {
            log.info("City table already populated, skipping import");
        }
    }
}
