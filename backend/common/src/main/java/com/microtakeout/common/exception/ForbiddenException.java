package com.microtakeout.common.exception;

/**
 * 禁止访问异常
 */
public final class ForbiddenException extends BusinessException {
    public ForbiddenException(String message) {
        super("403", message);
    }

    public ForbiddenException() {
        super("403", "权限不足");
    }
}

