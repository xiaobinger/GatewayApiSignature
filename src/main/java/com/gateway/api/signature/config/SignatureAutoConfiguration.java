package com.gateway.api.signature.config;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiongbing
 * @date 2025/8/15 9:49
 * @description
 */
@Configuration
@ConditionalOnProperty(prefix = "security.signature", name = "enable", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.gateway.api.signature")
public class SignatureAutoConfiguration {

}
