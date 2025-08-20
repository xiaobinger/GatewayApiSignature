package com.gateway.api.signature.util;

import java.util.*;

/**
 * @author chenpengcheng
 * @date 2025/3/18
 */
public class SignUtil {

    public static String buildSignString(Map<String, Object> params) {
        List<String> keyValuePairs = new ArrayList<>();
        processObject("", params, keyValuePairs);
        Collections.sort(keyValuePairs);
        return String.join("&", keyValuePairs);
    }

    private static void processObject(String prefix, Object value, List<String> keyValuePairs) {
        if (value == null) {
            return;
        }
        if (value instanceof Map) {
            // 处理对象
            Map<?, ?> map = (Map<?, ?>) value;
            List<Map.Entry<?, ?>> entries = new ArrayList<>(map.entrySet());
            entries.sort(Comparator.comparing(e -> e.getKey().toString()));

            for (Map.Entry<?, ?> entry : entries) {
                String key = entry.getKey().toString();
                Object val = entry.getValue();
                String newPrefix = prefix.isEmpty() ? key : prefix + "." + key;
                processObject(newPrefix, val, keyValuePairs);
            }
        } else if (value instanceof List) {
            // 处理数组
            List<?> list = (List<?>) value;
            for (int i = 0; i < list.size(); i++) {
                String newPrefix = prefix + "[" + i + "]";
                processObject(newPrefix, list.get(i), keyValuePairs);
            }
        } else {
            // 基本类型处理
            keyValuePairs.add(prefix + "=" + value);
        }
    }
}
