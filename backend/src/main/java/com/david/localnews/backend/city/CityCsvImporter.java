package com.david.localnews.backend.city;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class CityCsvImporter {

    private final CityRepository cityRepository;

    @Transactional
    public void importIfEmpty() {
        long count = cityRepository.count();
        if (count > 0) {
            log.info("City table already has {} rows. Skip import.", count);
            return;
        }

        String projectRoot = System.getProperty("user.dir"); // .../local-news/backend
        String path = projectRoot.replace("\\", "/")
                .replaceFirst("/backend$", "") + "/data/us_cities.csv";

        log.info("Import cities from: {}", path);

        AtomicInteger inserted = new AtomicInteger();

        try (var reader = new BufferedReader(
                new InputStreamReader(new java.io.FileInputStream(path), StandardCharsets.UTF_8))) {

            String header = reader.readLine();
            if (header == null || !header.toLowerCase().startsWith("city,")) {
                throw new IllegalStateException("us_cities.csv: wrong or missing header line");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 6) continue;

                String city = p[0].trim();
                String stateId = p[1].trim();
                String stateName = p[2].trim();
                Double lat = parseDouble(p[3]);
                Double lon = parseDouble(p[4]);
                Integer population = parseInt(p[5]);

                if (city.isEmpty() || stateId.isEmpty() || stateName.isEmpty()) continue;

                City c = City.builder()
                        .name(city)
                        .stateId(stateId)
                        .stateName(stateName)
                        .lat(lat)
                        .lon(lon)
                        .population(population)
                        .build();

                cityRepository.save(c);
                inserted.incrementAndGet();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to import cities from " + path, e);
        }

        log.info("Imported {} cities", inserted.get());
    }

    private static Double parseDouble(String s) {
        try { return s == null || s.isBlank() ? null : Double.parseDouble(s); }
        catch (Exception e) { return null; }
    }
    private static Integer parseInt(String s) {
        try { return s == null || s.isBlank() ? null : Integer.parseInt(s); }
        catch (Exception e) { return null; }
    }
}
