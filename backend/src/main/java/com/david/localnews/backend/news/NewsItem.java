package com.david.localnews.backend.news;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "news_item",
        indexes = {
                @Index(name = "idx_news_item_city", columnList = "city_id"),
                @Index(name = "idx_news_item_is_local", columnList = "is_local"),
                @Index(name = "idx_news_item_decided", columnList = "decided_at DESC")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class NewsItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "raw_id", unique = true, nullable = false)
    private Long rawId;                     // <-- Long

    @Column(length = 512, nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(length = 1024, nullable = false)
    private String url;

    @Column(length = 256)
    private String source;

    @Column(name = "is_local", nullable = false)
    private boolean isLocal;

    @Column(name = "city_id")
    private Long cityId;

    @Column(nullable = false)
    private Integer confidence = 0;

    @CreationTimestamp
    @Column(name = "decided_at", nullable = false, updatable = false)
    private Instant decidedAt;
}