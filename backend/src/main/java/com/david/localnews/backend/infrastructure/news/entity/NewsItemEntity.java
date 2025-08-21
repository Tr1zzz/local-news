package com.david.localnews.backend.infrastructure.news.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "news_item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(of = {"id","title","isLocal"})
@EqualsAndHashCode(of = "id")
public class NewsItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "raw_id", unique = true, nullable = false)
    private Long rawId;

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
