package com.gateway.api.signature.exception;

import com.gateway.api.signature.common.ErrorCode;

/**
 * @author xiongbing
 * @date 2025/8/15 9:50
 * @description 自定义异常类
 */
public class SignatureException extends RuntimeException {
    public SignatureException(String message) {
        super(message);
    }

    public SignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignatureException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
