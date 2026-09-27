package com.ss17hw02lamgiahuyptit015.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình Cache cho ứng dụng và kích hoạt @EnableCaching
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Khởi tạo In-Memory CacheManager dựa trên ConcurrentMapCacheManager
     * Đảm bảo lưu trữ cache thread-safe và tốc độ đọc tức thì (< 20ms)
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("restaurantMenu");
    }
}
