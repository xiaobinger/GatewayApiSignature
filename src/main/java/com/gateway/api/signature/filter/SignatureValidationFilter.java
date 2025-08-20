package com.gateway.api.signature.filter;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.api.signature.config.SignatureProperties;
import com.gateway.api.signature.exception.SignatureException;
import com.gateway.api.signature.util.RSAUtil;
import com.gateway.api.signature.util.SignUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiongbing
 * @date 2025/8/15 13:40
 * @description
 */
@Slf4j
@Component
@AllArgsConstructor
public class SignatureValidationFilter extends CommonFilter implements GlobalFilter, Ordered  {
    private final SignatureProperties signatureProperties;
    private static final String TIMESTAMP_HEADER = "X-Timestamp";
    private static final String SIGNATURE_HEADER = "X-Sign";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 检查是否启用签名验证
        if (!signatureProperties.isEnable()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        // 检查路径是否需要验证签名
        if (shouldValidateSignature(path, signatureProperties.getIncludePaths(), signatureProperties.getExcludePaths())) {
            return chain.filter(exchange);
        }
        //防重放攻击
        replayAttack(exchange,signatureProperties.getExpireTime());
        return verifySignature(exchange, chain);
    }

    private Mono<Void> verifySignature(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (HttpMethod.POST.equals(request.getMethod())) {
            return DataBufferUtils.join(request.getBody())
                    .flatMap(dataBuffer -> {
                        try {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            String body = new String(bytes, StandardCharsets.UTF_8);
                            boolean isValid = verifySignature(body, request);
                            if (!isValid) {
                                return Mono.error(new SignatureException("签名验证失败"));
                            }
                            // 缓存请求体以便重复读取
                            Flux<DataBuffer> cachedBody = Flux.just(exchange.getResponse().bufferFactory().wrap(bytes));
                            ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(request) {
                                @Override
                                public Flux<DataBuffer> getBody() {
                                    return cachedBody;
                                }
                            };
                            return chain.filter(exchange.mutate().request(decorator).build());
                        }catch (Exception e){
                            return Mono.error(new SignatureException("资源读取失败"));
                        }finally {
                            //释放资源
                            DataBufferUtils.release(dataBuffer);
                        }
                    });
        }else if (HttpMethod.GET.equals(request.getMethod())) {
            // 获取URL中的查询参数
            boolean isValid = verifySignature(null, request);
            if (!isValid) {
                return Mono.error(new SignatureException("签名验证失败"));
            }
            return chain.filter(exchange);
        }else {
            return chain.filter(exchange);
        }
    }



    private boolean verifySignature(String body, ServerHttpRequest request) {
        String path = request.getURI().getPath();
        try {
            String sign = request.getHeaders().getFirst(SIGNATURE_HEADER);
            if (StrUtil.isBlank(sign)){
                throw new SignatureException("签名为空");
            }
            // 验签
            // 先获取URL中的查询参数
            Map<String, Object> paramMap = new HashMap<>(request.getQueryParams().toSingleValueMap());
            if (StrUtil.isNotBlank(body)){
                Map<String, Object> param = objectMapper.readValue(body, Map.class);
                paramMap.putAll(param);
            }
            String timestamp = request.getHeaders().getFirst(TIMESTAMP_HEADER);
            paramMap.put("timestamp", timestamp);
            String bodyMap = SignUtil.buildSignString(paramMap);
            boolean verify = RSAUtil.verifySignature(bodyMap, sign,
                    signatureProperties.getPublicKey(),signatureProperties.getAlgorithm());
            if (!verify){
                log.warn("API验签失败！！！请求路径：{}", path);
            }
            if (signatureProperties.isLogEnable()) {
                log.info("请求路径：{}，验签结果：{}", request.getURI().getPath(), verify);
            }
            return verify;
        }catch (SignatureException ge){
            throw ge;
        }catch (Exception e){
            log.error("API验签异常，请求路径：{}", path, e);
            throw new SignatureException("非法请求");
        }
    }


    private static void replayAttack(ServerWebExchange exchange,Integer expireTime) {
        ServerHttpRequest request = exchange.getRequest();
        // 从请求头中获取时间戳
        String timestamp = request.getHeaders().getFirst(TIMESTAMP_HEADER);
        if (StrUtil.isBlank(timestamp) || !timestamp.matches("^\\d+$")){
            throw new SignatureException("非法的时间戳");
        }
        // 校验时间戳是否过期(请求超过60s视为过期)
        long currentTime = System.currentTimeMillis();
        long requestTime = Long.parseLong(timestamp);
        if (currentTime - requestTime > expireTime * 1000) {
            throw new SignatureException("请求已过期");
        }
    }

    @Override
    public int getOrder() {
        // 设置在缓存过滤器之后执行
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
