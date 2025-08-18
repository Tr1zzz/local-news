package com.david.localnews.backend;

import com.david.localnews.backend.city.CityCsvImporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {

    private final CityCsvImporter cityCsvImporter;

    @Override
    public void run(String... args) {
        cityCsvImporter.importIfEmpty();
    }
}