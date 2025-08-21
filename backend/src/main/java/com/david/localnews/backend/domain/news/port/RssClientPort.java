package com.david.localnews.backend.domain.news.port;

import com.david.localnews.backend.domain.news.enums.RssFeedType;
import com.david.localnews.backend.domain.news.model.RawNews;

import java.util.List;

public interface RssClientPort {
    List<RawNews> fetchOne(String feedUrl, RssFeedType type, int maxPerFeed);
}