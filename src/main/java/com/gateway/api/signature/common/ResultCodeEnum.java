package com.gateway.api.signature.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 枚举了一些常用API操作码
 *
 * @author code
 */
@Getter
@AllArgsConstructor
public enum ResultCodeEnum implements ErrorCode {

    /**
     * 通用枚举
     */
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(400, "参数检验失败"),
    ARGUMENT_TYPE_MISMATCH(400, "参数类型不匹配"),
    PARAM_FORMAT_ERROR(400, "参数格式错误"),
    VALID_TYPE_MISMATCH(400, "校验类型不支持"),
    UNAUTHORIZED(401, "登录已过期，请重新登陆"),
    FORBIDDEN(403, "没有相关权限"),

    REQ_NOT_SUPPORT(405, "请求方式不支持"),
    NOT_ACCEPTABLE(406, "服务繁忙，请稍后重试");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;


}
