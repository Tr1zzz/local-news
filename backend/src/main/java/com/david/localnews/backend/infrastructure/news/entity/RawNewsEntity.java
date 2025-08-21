package com.david.localnews.backend.infrastructure.news.entity;

import com.david.localnews.backend.domain.news.enums.RssFeedType;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Entity
@Table(name = "news_raw", uniqueConstraints = {
        @UniqueConstraint(name = "uk_news_raw_url", columnNames = {"url"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(of = {"id","title"})
@EqualsAndHashCode(of = "id")
public class RawNewsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String title;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(nullable = false, length = 1024)
    private String url;

    @Column(nullable = false, length = 256)
    private String source;

    private Instant publishedAt;

    @Column(nullable = false)
    private Instant fetchedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private RssFeedType feedType;
}