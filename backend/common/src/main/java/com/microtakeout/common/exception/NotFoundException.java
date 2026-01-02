package com.microtakeout.common.exception;

/**
 * 资源未找到异常
 */
public final class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super("404", message);
    }

    public NotFoundException(String resource, Object id) {
        super("404", String.format("%s with id %s not found", resource, id));
    }
}

