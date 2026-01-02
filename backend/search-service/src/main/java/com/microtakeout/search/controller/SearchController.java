package com.microtakeout.search.controller;

import com.microtakeout.common.dto.ApiResponse;
import com.microtakeout.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/restaurants")
    public ApiResponse<List<Map<String, Object>>> searchRestaurants(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Double latitude,
        @RequestParam(required = false) Double longitude) {
        List<Map<String, Object>> results = searchService.searchRestaurants(keyword, latitude, longitude);
        return ApiResponse.success(results);
    }
}

