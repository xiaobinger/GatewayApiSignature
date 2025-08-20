package com.gateway.api.signature.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author xiongbing
 * @date 2025/8/15 9:49
 * @description 配置类
 */
@Component
@ConfigurationProperties(prefix = "security.signature")
@Data
public class SignatureProperties {

    private boolean enable = true;

    private String publicKey;
    /**
     * 过期时间，默认60秒
     */
    private Integer expireTime = 60;
    /**
     * 签名算法
     */
    private String algorithm = "SHA256withRSA";

    private boolean logEnable;

    private List<String> includePaths;

    private List<String> excludePaths;
}
