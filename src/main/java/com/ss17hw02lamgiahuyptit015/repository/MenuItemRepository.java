package com.ss17hw02lamgiahuyptit015.repository;

import com.ss17hw02lamgiahuyptit015.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository truy xuất dữ liệu từ bảng menu_items
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    /**
     * Tìm danh sách món ăn theo restaurantId
     *
     * @param restaurantId mã nhà hàng
     * @return danh sách MenuItem của nhà hàng
     */
    List<MenuItem> findByRestaurantId(Long restaurantId);
}
