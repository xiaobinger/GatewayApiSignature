package com.gateway.api.signature.exception;

import cn.hutool.json.JSONUtil;
import com.gateway.api.signature.common.ResultVO;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局异常处理器
 * @author chenpengcheng
 * @date 2025/3/11
 */
@Component
@Order(-1)
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // 根据异常类型设置响应状态码和消息
        if (ex instanceof SignatureException) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return writeResponse(exchange, ex.getLocalizedMessage());
        }

        if (ex instanceof NotFoundException) {
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            return writeResponse(exchange, "not found");
        }

        // 默认异常处理
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return writeResponse(exchange, ex.getLocalizedMessage());
    }

    private Mono<Void> writeResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ResultVO<Object> result = ResultVO.failed(message);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory()
                .wrap(JSONUtil.toJsonStr(result).getBytes())));
    }
}
