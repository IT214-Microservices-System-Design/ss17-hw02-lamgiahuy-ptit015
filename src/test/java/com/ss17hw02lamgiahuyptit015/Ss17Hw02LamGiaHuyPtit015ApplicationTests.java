package com.ss17hw02lamgiahuyptit015;

import com.ss17hw02lamgiahuyptit015.entity.MenuItem;
import com.ss17hw02lamgiahuyptit015.service.RestaurantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;

@SpringBootTest
class Ss17Hw02LamGiaHuyPtit015ApplicationTests {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("Kiểm tra toàn diện cơ chế Caching @Cacheable(value = 'restaurantMenu', key = '#id')")
    void contextLoadsAndCacheWorks() {
        Long restaurantId = 101L;
        Cache cache = cacheManager.getCache("restaurantMenu");
        Assertions.assertNotNull(cache, "CacheManager phải quản lý cache tên 'restaurantMenu'");

        // Xóa cache trước khi kiểm thử
        cache.clear();

        // Lần 1: Chưa có trong cache (Cache Miss) -> Phải gọi DB (chậm ~ 3 giây)
        long start1 = System.currentTimeMillis();
        List<MenuItem> list1 = restaurantService.getMenuByRestaurantId(restaurantId);
        long duration1 = System.currentTimeMillis() - start1;

        Assertions.assertNotNull(list1);
        Assertions.assertEquals(2, list1.size());
        Assertions.assertTrue(duration1 >= 3000, "Lần gọi 1 phải >= 3000ms do độ trễ DB");

        // Kiểm tra cache đã được lưu với key = 101
        Cache.ValueWrapper wrapper = cache.get(restaurantId);
        Assertions.assertNotNull(wrapper, "Dữ liệu phải được lưu trong cache với key là restaurantId (101)");

        // Lần 2: Đã có trong cache (Cache Hit) -> Phản hồi tức thì < 20ms
        long start2 = System.currentTimeMillis();
        List<MenuItem> list2 = restaurantService.getMenuByRestaurantId(restaurantId);
        long duration2 = System.currentTimeMillis() - start2;

        Assertions.assertNotNull(list2);
        Assertions.assertEquals(list1.size(), list2.size());
        Assertions.assertEquals(list1.get(0).getDishName(), list2.get(0).getDishName());
        Assertions.assertTrue(duration2 < 50, "Lần gọi 2 phải < 50ms (thực tế < 20ms), thực tế đạt: " + duration2 + "ms");
    }
}
