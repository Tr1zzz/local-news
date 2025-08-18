package com.david.localnews.backend.news;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "news_raw", uniqueConstraints = {
        @UniqueConstraint(name = "uk_news_raw_url", columnNames = {"url"})
}, indexes = {
        @Index(name = "idx_news_raw_published", columnList = "publishedAt"),
        @Index(name = "idx_news_raw_feedtype", columnList = "feedType")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RawNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Заголовок */
    @Column(nullable = false, length = 512)
    private String title;

    /** Короткое описание/вступление (plain text) */
    @Column(columnDefinition = "text")
    private String summary;

    /** Ссылка на оригинал */
    @Column(nullable = false, length = 1024)
    private String url;

    /** Источник (домен/название ленты) */
    @Column(nullable = false, length = 256)
    private String source;

    /** Когда статья опубликована у источника */
    private Instant publishedAt;

    /** Когда мы забрали запись */
    @Column(nullable = false)
    private Instant fetchedAt;

    /** Тип ленты, откуда пришла запись: локальная-кандидат или глобальная */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private RssFeedType feedType;
}