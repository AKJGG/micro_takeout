package com.microtakeout.restaurant.controller;

import com.microtakeout.common.dto.ApiResponse;
import com.microtakeout.common.dto.PageResult;
import com.microtakeout.restaurant.entity.MenuItem;
import com.microtakeout.restaurant.entity.Restaurant;
import com.microtakeout.restaurant.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 餐厅控制器
 */
@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;

    @PostMapping
    public ApiResponse<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        Restaurant created = restaurantService.createRestaurant(restaurant);
        return ApiResponse.success("餐厅创建成功", created);
    }

    @GetMapping("/{id}")
    public ApiResponse<Restaurant> getRestaurantById(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        return ApiResponse.success(restaurant);
    }

    @GetMapping
    public ApiResponse<PageResult<Restaurant>> getRestaurants(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String status) {
        PageResult<Restaurant> result = restaurantService.getRestaurants(page, size, status);
        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    public ApiResponse<Restaurant> updateRestaurant(
        @PathVariable Long id,
        @RequestBody Restaurant restaurant,
        @RequestHeader("X-User-Id") Long userId) {
        Restaurant updated = restaurantService.updateRestaurant(id, restaurant, userId);
        return ApiResponse.success("餐厅更新成功", updated);
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<Object> approveRestaurant(@PathVariable Long id) {
        restaurantService.approveRestaurant(id);
        return ApiResponse.success("餐厅审批成功");
    }

    @GetMapping("/{id}/menu")
    public ApiResponse<List<MenuItem>> getMenuItems(@PathVariable Long id) {
        List<MenuItem> items = restaurantService.getMenuItems(id);
        return ApiResponse.success(items);
    }

    @PostMapping("/{id}/menu")
    public ApiResponse<MenuItem> createMenuItem(
        @PathVariable Long id,
        @RequestBody MenuItem menuItem) {
        menuItem.setRestaurantId(id);
        MenuItem created = restaurantService.createMenuItem(menuItem);
        return ApiResponse.success("菜单项创建成功", created);
    }
}

