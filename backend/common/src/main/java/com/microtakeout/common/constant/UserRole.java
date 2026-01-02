package com.microtakeout.common.constant;

/**
 * 用户角色枚举
 */
public enum UserRole {
    ADMIN("管理员"),
    CUSTOMER("顾客"),
    RESTAURANT_OWNER("餐厅老板"),
    DELIVERY_PERSON("配送员");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

