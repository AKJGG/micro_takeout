package com.microtakeout.common.exception;

/**
 * 未授权异常
 */
public final class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super("401", message);
    }

    public UnauthorizedException() {
        super("401", "未授权，请先登录");
    }
}

