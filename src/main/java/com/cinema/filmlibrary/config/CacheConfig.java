package com.cinema.filmlibrary.config;

import com.cinema.filmlibrary.entity.Director;
import com.cinema.filmlibrary.entity.Film;
import com.cinema.filmlibrary.entity.Review;
import java.util.List;
import com.cinema.filmlibrary.utils.CacheUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Class to store cache. */
@Configuration
public class CacheConfig {

    /** Bean for film cache (requests by id). */
    @Bean
    public CacheUtil<Long, Film> filmCacheId() {
        return new CacheUtil<>(20);
    }

    /** Bean for director cache (requests by id). */
    @Bean
    public CacheUtil<Long, Director> directorCacheId() {
        return new CacheUtil<>(10);
    }

    /** Bean for review cache (requests by id). */
    @Bean
    public CacheUtil<Long, List<Review>> reviewCacheId() {
        return new CacheUtil<>(10);
    }
}
