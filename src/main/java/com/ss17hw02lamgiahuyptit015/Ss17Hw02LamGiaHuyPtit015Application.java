package com.ss17hw02lamgiahuyptit015;

import com.ss17hw02lamgiahuyptit015.service.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class Ss17Hw02LamGiaHuyPtit015Application {

    public static void main(String[] args) {
        SpringApplication.run(Ss17Hw02LamGiaHuyPtit015Application.class, args);
    }

    /**
     * Demo tự động kiểm thử Lazy Loading và @Cacheable ngay khi ứng dụng khởi chạy
     */
    @Bean
    public CommandLineRunner demoRunner(RestaurantService restaurantService) {
        return args -> {
            log.info("\n================================================================================");
            log.info(">>> DEMO BÀI TẬP 2: TRIỂN KHAI LAZY LOADING VỚI @Cacheable (KEY = #id) <<<");
            log.info("================================================================================");

            Long restaurantId = 101L;

            log.info(">>> [LẦN GỌI THỨ NHẤT] - Cache Miss (Chưa có cache, thực thi hàm và ngủ 3000ms):");
            long start1 = System.currentTimeMillis();
            restaurantService.getMenuByRestaurantId(restaurantId);
            long duration1 = System.currentTimeMillis() - start1;
            log.info(">>> KẾT QUẢ LẦN 1: Thời gian đáp ứng = {} ms (Dự kiến >= 3000 ms)", duration1);

            log.info("\n--------------------------------------------------------------------------------");
            log.info(">>> [LẦN GỌI THỨ HAI] - Cache Hit (Đã có cache, trả kết quả ngay tức thì):");
            long start2 = System.currentTimeMillis();
            restaurantService.getMenuByRestaurantId(restaurantId);
            long duration2 = System.currentTimeMillis() - start2;
            log.info(">>> KẾT QUẢ LẦN 2: Thời gian đáp ứng = {} ms (Dự kiến < 20 ms)", duration2);
            log.info("================================================================================\n");
        };
    }
}
