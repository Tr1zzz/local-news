package com.david.localnews.backend.bootstrap;

import com.david.localnews.backend.domain.city.port.CityLookupPort;
import com.david.localnews.backend.domain.city.port.CityPort;
import com.david.localnews.backend.domain.city.port.ImportCitiesPort;
import com.david.localnews.backend.domain.city.usecase.implementation.ImportCitiesUseCaseHandler;
import com.david.localnews.backend.domain.city.usecase.implementation.SearchCitiesUseCaseHandler;
import com.david.localnews.backend.domain.city.usecase.interfaces.ImportCitiesUseCase;
import com.david.localnews.backend.domain.city.usecase.interfaces.SearchCitiesUseCase;
import com.david.localnews.backend.domain.news.port.NewsClassifierPort;
import com.david.localnews.backend.domain.news.port.NewsPort;
import com.david.localnews.backend.domain.news.port.RawNewsPort;
import com.david.localnews.backend.domain.news.port.RssClientPort;
import com.david.localnews.backend.domain.news.usecase.implementation.ClassifyNewsUseCaseHandler;
import com.david.localnews.backend.domain.news.usecase.implementation.GetNewsUseCaseHandler;
import com.david.localnews.backend.domain.news.usecase.implementation.IngestRssUseCaseHandler;
import com.david.localnews.backend.domain.news.usecase.interfaces.ClassifyNewsUseCase;
import com.david.localnews.backend.domain.news.usecase.interfaces.GetNewsUseCase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BeanConfig {

    @Value("${app.ingest.topCities:100}")
    private int topCities;

    @Value("${app.ingest.delayMsBetweenCalls:200}")
    private long delayMs;

    @Value("${app.ingest.local.maxPerFeed:2}")
    private int localMaxPerFeed;

    @Value("${app.ingest.global.maxPerFeed:8}")
    private int globalMaxPerFeed;

    @Value("${app.ingest.templates.googleCity}")
    private String googleCityTemplate;

    @Value("#{'${app.rss.global}'.split(',')}")
    private List<String> globalFeeds;

    @Bean
    public SearchCitiesUseCase searchCitiesUseCase(CityPort repo) {
        return new SearchCitiesUseCaseHandler(repo);
    }

    @Bean
    public ImportCitiesUseCase importCitiesUseCase(ImportCitiesPort importer) {
        return new ImportCitiesUseCaseHandler(importer);
    }

    @Bean
    public GetNewsUseCase getNewsUseCase(NewsPort repo) {
        return new GetNewsUseCaseHandler(repo);
    }

    @Bean
    public ClassifyNewsUseCase classifyNewsUseCase(
            NewsClassifierPort classifier,
            RawNewsPort rawRepo,
            NewsPort newsRepo,
            CityLookupPort cityLookup
    ) {
        return new ClassifyNewsUseCaseHandler(classifier, rawRepo, newsRepo, cityLookup);
    }

    @Bean
    public IngestRssUseCaseHandler ingestStatsHandler(
            RssClientPort rss,
            RawNewsPort rawRepo,
            CityPort cityPort
    ) {
        return new IngestRssUseCaseHandler(
                rss, rawRepo, cityPort,
                topCities, delayMs, localMaxPerFeed, globalMaxPerFeed,
                googleCityTemplate, globalFeeds
        );
    }
}
