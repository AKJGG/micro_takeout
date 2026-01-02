package com.microtakeout.restaurant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.microtakeout.common.dto.PageResult;
import com.microtakeout.common.exception.ForbiddenException;
import com.microtakeout.common.exception.NotFoundException;
import com.microtakeout.restaurant.entity.MenuItem;
import com.microtakeout.restaurant.entity.Restaurant;
import com.microtakeout.restaurant.mapper.MenuItemMapper;
import com.microtakeout.restaurant.mapper.RestaurantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 餐厅服务
 */
@Service
public class RestaurantService {
    private static final Logger log = LoggerFactory.getLogger(RestaurantService.class);
    
    @Autowired
    private RestaurantMapper restaurantMapper;
    
    @Autowired
    private MenuItemMapper menuItemMapper;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Restaurant createRestaurant(Restaurant restaurant) {
        restaurant.setStatus("PENDING");
        restaurantMapper.insert(restaurant);
        
        // 发送餐厅创建事件
        kafkaTemplate.send("restaurant-events", Map.of(
            "eventType", "RESTAURANT_CREATED",
            "restaurantId", restaurant.getId(),
            "name", restaurant.getName()
        ));
        
        log.info("餐厅创建成功: {}", restaurant.getName());
        return restaurant;
    }

    public Restaurant getRestaurantById(Long id) {
        Restaurant restaurant = restaurantMapper.selectById(id);
        if (restaurant == null) {
            throw new NotFoundException("餐厅", id);
        }
        return restaurant;
    }

    public PageResult<Restaurant> getRestaurants(int page, int size, String status) {
        Page<Restaurant> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Restaurant> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Restaurant::getStatus, status);
        }
        Page<Restaurant> result = restaurantMapper.selectPage(pageParam, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Transactional
    public Restaurant updateRestaurant(Long id, Restaurant restaurant, Long userId) {
        Restaurant existing = getRestaurantById(id);
        if (!existing.getOwnerId().equals(userId)) {
            throw new ForbiddenException("无权修改此餐厅");
        }
        
        restaurant.setId(id);
        restaurantMapper.updateById(restaurant);
        
        // 发送更新事件
        kafkaTemplate.send("restaurant-events", Map.of(
            "eventType", "RESTAURANT_UPDATED",
            "restaurantId", id
        ));
        
        return restaurant;
    }

    @Transactional
    public void approveRestaurant(Long id) {
        Restaurant restaurant = getRestaurantById(id);
        restaurant.setStatus("APPROVED");
        restaurantMapper.updateById(restaurant);
        
        // 发送审批事件
        kafkaTemplate.send("restaurant-events", Map.of(
            "eventType", "RESTAURANT_APPROVED",
            "restaurantId", id
        ));
    }

    public List<MenuItem> getMenuItems(Long restaurantId) {
        LambdaQueryWrapper<MenuItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MenuItem::getRestaurantId, restaurantId);
        return menuItemMapper.selectList(wrapper);
    }

    @Transactional
    public MenuItem createMenuItem(MenuItem menuItem) {
        menuItemMapper.insert(menuItem);
        
        // 发送菜单项创建事件
        kafkaTemplate.send("menu-events", Map.of(
            "eventType", "MENU_ITEM_CREATED",
            "menuItemId", menuItem.getId(),
            "restaurantId", menuItem.getRestaurantId()
        ));
        
        return menuItem;
    }

    @Transactional
    public void updateStock(Long menuItemId, Integer quantity) {
        MenuItem item = menuItemMapper.selectById(menuItemId);
        if (item == null) {
            throw new NotFoundException("菜单项", menuItemId);
        }
        item.setStock(item.getStock() - quantity);
        menuItemMapper.updateById(item);
    }
}

