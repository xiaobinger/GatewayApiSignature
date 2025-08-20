package com.gateway.api.signature.common;

/**
 * 封装API的错误码
 *
 * @author code
 */
public interface ErrorCode {

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    int getCode();

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    String getMessage();

}
