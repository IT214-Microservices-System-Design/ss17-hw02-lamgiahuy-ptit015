package com.ss17hw02lamgiahuyptit015.controller;

import com.ss17hw02lamgiahuyptit015.entity.MenuItem;
import com.ss17hw02lamgiahuyptit015.service.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller cung cấp REST API lấy thực đơn của nhà hàng
 */
@RestController
@RequestMapping("/restaurants")
@Slf4j
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /**
     * Endpoint lấy danh sách thực đơn theo ID nhà hàng
     * GET /restaurants/{restaurantId}/menu
     *
     * @param restaurantId mã nhà hàng
     * @return danh sách MenuItem kèm mã HTTP 200 OK
     */
    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<MenuItem>> getMenu(@PathVariable Long restaurantId) {
        long startTime = System.currentTimeMillis();
        log.info("--> [API REQUEST] Nhận request GET /restaurants/{}/menu", restaurantId);

        List<MenuItem> menu = restaurantService.getMenuByRestaurantId(restaurantId);

        long duration = System.currentTimeMillis() - startTime;
        log.info("--> [API RESPONSE] Hoàn thành request GET /restaurants/{}/menu trong {} ms (Số món: {})", 
                restaurantId, duration, menu.size());

        return ResponseEntity.ok(menu);
    }
}
