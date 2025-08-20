package com.gateway.api.signature.filter;

import java.util.List;

/**
 * @author xiongbing
 * @date 2025/8/15 16:10
 * @description
 */
public class CommonFilter {

    /**
     * 判断是否需要验证签名
     * @param path 请求路径
     * @return 是否需要验证
     */
    protected boolean shouldValidateSignature(String path,List<String> includePaths,List<String> excludePaths) {
        // 如果配置了排除路径且当前路径匹配，则不验证
        if (excludePaths != null) {
            for (String excludePath : excludePaths) {
                if (pathMatcher(path, excludePath)) {
                    return true;
                }
            }
        }
        // 如果没有配置包含路径，则默认都需要验证
        if (includePaths == null || includePaths.isEmpty()) {
            return false;
        }
        // 如果配置了包含路径，则只有匹配的路径才验证
        for (String includePath : includePaths) {
            if (pathMatcher(path, includePath)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 路径匹配器（简单实现）
     * @param path 请求路径
     * @param pattern 匹配模式
     * @return 是否匹配
     */
    private boolean pathMatcher(String path, String pattern) {
        // 支持通配符匹配
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }
        return path.equals(pattern);
    }

}
