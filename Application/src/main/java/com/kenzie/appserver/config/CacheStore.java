package com.kenzie.appserver.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.kenzie.appserver.controller.model.GameSummaryResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CacheStore {
    private final Cache<String, List<GameSummaryResponse>> cache;

    public CacheStore(int expiry, TimeUnit timeUnit) {
        // initalize the cache
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(expiry, timeUnit)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build();    
    }

    public List<GameSummaryResponse> get(String key) {
        // Write your code here
        // Retrieve and return the list of responses
        return cache.getIfPresent(key);
    }

    public void evict(String key) {
        // Write your code here
        // Invalidate/evict the list from cache
        cache.invalidate(key);
    }

    public void add(String key, List<GameSummaryResponse> value) {
        // Write your code here
        // Add list of responses to cache
        cache.put(key, value);
    }
}
