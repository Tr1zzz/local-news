package com.david.localnews.backend.domain.news.port;

public interface NewsClassifierPort {
    Classification classify(String title, String summary, String source);
    record Classification(boolean isLocal, String city, String state, int confidence) {}
}