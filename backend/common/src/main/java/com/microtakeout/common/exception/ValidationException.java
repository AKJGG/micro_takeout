package com.microtakeout.common.exception;

/**
 * 验证异常
 */
public final class ValidationException extends BusinessException {
    public ValidationException(String message) {
        super("400", message);
    }
}

