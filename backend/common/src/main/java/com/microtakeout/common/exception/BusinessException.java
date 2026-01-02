package com.microtakeout.common.exception;

/**
 * 业务异常
 */
public sealed class BusinessException extends RuntimeException permits 
    NotFoundException, 
    ValidationException, 
    UnauthorizedException, 
    ForbiddenException {
    
    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

