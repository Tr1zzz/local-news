package com.david.localnews.backend.news;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsItemRepository extends JpaRepository<NewsItem, Long> {

    boolean existsByRawId(Long rawId);

    Page<NewsItem> findByCityIdOrderByDecidedAtDesc(Long cityId, Pageable pageable);

    Page<NewsItem> findByIsLocalOrderByDecidedAtDesc(boolean isLocal, Pageable pageable);

    // новое: весь фид без фильтра "local/global"
    Page<NewsItem> findAllByOrderByDecidedAtDesc(Pageable pageable);
}
