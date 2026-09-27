package com.ss17hw02lamgiahuyptit015.service;

import com.ss17hw02lamgiahuyptit015.entity.MenuItem;
import com.ss17hw02lamgiahuyptit015.repository.MenuItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service quản lý thực đơn nhà hàng với chiến lược Lazy Loading qua @Cacheable
 */
@Service
@Slf4j
public class RestaurantService {

    private final MenuItemRepository menuItemRepository;

    public RestaurantService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    /**
     * Lấy danh sách thực đơn theo ID nhà hàng.
     * Sử dụng @Cacheable để lưu kết quả vào cache "restaurantMenu" với dynamic key là "#id".
     *
     * - Cache Miss (Lần gọi đầu tiên): Thực thi thân hàm, giả lập độ trễ DB (Thread.sleep 3s),
     *   truy vấn cơ sở dữ liệu và tự động ghi kết quả vào Cache.
     * - Cache Hit (Các lần gọi tiếp theo cùng id): Spring Cache AOP proxy chặn lại và trả về
     *   kết quả trực tiếp từ Cache trong thời gian < 20ms mà KHÔNG thực thi thân hàm (không qua DB).
     *
     * @param id mã định danh nhà hàng (restaurantId)
     * @return danh sách các món ăn trong thực đơn
     */
    @Cacheable(value = "restaurantMenu", key = "#id")
    public List<MenuItem> getMenuByRestaurantId(Long id) {
        // Log báo hiệu Cache Miss - luồng thực sự đi vào truy vấn Database
        log.info("==> [DATABASE QUERY - CACHE MISS] Bắt đầu truy vấn thực đơn cho restaurantId: {} từ Database...", id);
        System.out.println("--> [DATABASE QUERY - CACHE MISS] Đang truy vấn menu cho restaurantId = " + id + " từ Database...");

        // Bước 3: Mô phỏng độ trễ truy vấn database (JOIN phức tạp) bằng Thread.sleep(3000)
        simulateDatabaseDelay();

        // Truy vấn dữ liệu thực tế từ Database thông qua Repository
        List<MenuItem> menuItems = menuItemRepository.findByRestaurantId(id);

        // Fallback dữ liệu mẫu theo đúng yêu cầu đề bài nếu database chưa có bản ghi
        if (menuItems == null || menuItems.isEmpty()) {
            menuItems = List.of(
                    MenuItem.builder().id(1L).restaurantId(id).dishName("Phở Bò").price(55000.0).build(),
                    MenuItem.builder().id(2L).restaurantId(id).dishName("Bún Chả").price(60000.0).build()
            );
        }

        log.info("==> [DATABASE QUERY COMPLETED] Đã tải thành công {} món ăn cho restaurantId: {}. Kết quả được lưu vào cache 'restaurantMenu' với key: {}", 
                menuItems.size(), id, id);
        System.out.println("--> [DATABASE QUERY FINISHED] Đã nạp " + menuItems.size() + " món ăn cho restaurantId = " + id + ". Đang nạp vào Cache...");

        return menuItems;
    }

    /**
     * Helper method giả lập thời gian xử lý phức tạp của Database (3 giây)
     */
    private void simulateDatabaseDelay() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread bị gián đoạn trong khi giả lập độ trễ Database", e);
        }
    }
}
