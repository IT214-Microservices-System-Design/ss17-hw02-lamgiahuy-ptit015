package com.ss17hw02lamgiahuyptit015.service;

import com.ss17hw02lamgiahuyptit015.entity.MenuItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử Unit / Integration Test cho RestaurantService với @Cacheable
 * Tuân thủ nghiêm ngặt mô hình AAA (Arrange - Act - Assert)
 */
@SpringBootTest
class RestaurantServiceTest {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        // Xóa cache trước mỗi test để đảm bảo môi trường độc lập
        Cache cache = cacheManager.getCache("restaurantMenu");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    @DisplayName("Kiểm thử Cache Miss ở lần gọi 1 và Cache Hit ở lần gọi 2")
    void testCacheMissAndHitFlow() {
        // ==========================================
        // 1. ARRANGE (Chuẩn bị)
        // ==========================================
        Long restaurantId = 101L;
        Cache cache = cacheManager.getCache("restaurantMenu");
        assertNotNull(cache, "Cache 'restaurantMenu' phải được khởi tạo");
        assertNull(cache.get(restaurantId), "Trước khi gọi, cache cho restaurantId = 101 phải rỗng");

        // ==========================================
        // 2. ACT (Lần 1 - Kịch bản Cache Miss)
        // ==========================================
        long startTime1 = System.currentTimeMillis();
        List<MenuItem> firstResult = restaurantService.getMenuByRestaurantId(restaurantId);
        long duration1 = System.currentTimeMillis() - startTime1;

        // ==========================================
        // 3. ASSERT (Kiểm chứng Lần 1)
        // ==========================================
        assertNotNull(firstResult, "Kết quả trả về không được null");
        assertEquals(2, firstResult.size(), "Nhà hàng 101 có 2 món ăn");
        assertTrue(duration1 >= 3000, "Thời gian lần 1 phải >= 3000ms do mô phỏng DB delay (thực tế: " + duration1 + "ms)");

        // Kiểm chứng dữ liệu đã được nạp tự động vào cache với key là id (101)
        Cache.ValueWrapper cachedValue = cache.get(restaurantId);
        assertNotNull(cachedValue, "Dữ liệu phải tồn tại trong cache sau lần gọi 1");
        @SuppressWarnings("unchecked")
        List<MenuItem> cachedList = (List<MenuItem>) cachedValue.get();
        assertNotNull(cachedList);
        assertEquals(2, cachedList.size());

        // ==========================================
        // 4. ACT (Lần 2 - Kịch bản Cache Hit)
        // ==========================================
        long startTime2 = System.currentTimeMillis();
        List<MenuItem> secondResult = restaurantService.getMenuByRestaurantId(restaurantId);
        long duration2 = System.currentTimeMillis() - startTime2;

        // ==========================================
        // 5. ASSERT (Kiểm chứng Lần 2)
        // ==========================================
        assertNotNull(secondResult, "Kết quả trả về từ cache không được null");
        assertEquals(firstResult.size(), secondResult.size(), "Dữ liệu lần 2 phải đồng nhất với lần 1");
        assertEquals(firstResult.get(0).getDishName(), secondResult.get(0).getDishName());
        assertTrue(duration2 < 50, "Thời gian lần 2 phải phản hồi tức thì (< 50ms, thực tế: " + duration2 + "ms)");
    }

    @Test
    @DisplayName("Kiểm thử Cache phân biệt các key khác nhau theo dynamic key #id")
    void testCacheKeyIsolation() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        Long restaurantIdA = 101L;
        Long restaurantIdB = 202L;
        Cache cache = Objects.requireNonNull(cacheManager.getCache("restaurantMenu"));

        // ==========================================
        // 2. ACT
        // ==========================================
        restaurantService.getMenuByRestaurantId(restaurantIdA);
        restaurantService.getMenuByRestaurantId(restaurantIdB);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(cache.get(restaurantIdA), "Key 101 phải được lưu riêng biệt trong cache");
        assertNotNull(cache.get(restaurantIdB), "Key 202 phải được lưu riêng biệt trong cache");
    }
}
