package com.david.localnews.backend.infrastructure.city.repository;

import com.david.localnews.backend.infrastructure.city.entity.CityEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CityJpaRepository extends JpaRepository<CityEntity, Long> {

    @Query("""
SELECT c FROM CityEntity c
WHERE LOWER(c.name) LIKE LOWER(CONCAT(:q, '%'))
OR LOWER(c.stateName) LIKE LOWER(CONCAT(:q, '%'))
OR LOWER(CONCAT(c.name, ', ', c.stateId)) LIKE LOWER(CONCAT(:q, '%'))
ORDER BY c.population DESC NULLS LAST, c.name ASC
""")
    Page<CityEntity> searchPrefix(@Param("q") String q, Pageable pageable);

    @Query("""
SELECT c FROM CityEntity c
WHERE LOWER(c.name) = LOWER(:name)
AND UPPER(c.stateId) = UPPER(:state)
""")
    Optional<CityEntity> findExact(@Param("name") String name, @Param("state") String state);

    @Query("""
SELECT c FROM CityEntity c
WHERE LOWER(c.name) = LOWER(:name)
ORDER BY c.population DESC NULLS LAST, c.name ASC
""")
    Optional<CityEntity> findBestByName(@Param("name") String name);

    @Query("""
SELECT c FROM CityEntity c
WHERE c.population IS NOT NULL
ORDER BY c.population DESC
""")
    Page<CityEntity> topByPopulation(Pageable pageable);
}