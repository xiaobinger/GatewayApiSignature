package com.gateway.api.signature.util;// src/main/java/com/example/security/util/a.java

import cn.hutool.core.util.HexUtil;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA加解密工具类
 * @author xiongbing
 * @date 2025/08/15
 */
public class RSAUtil {

    /**
     * 验证RSA签名
     * @param data 数据
     * @param sign 签名
     * @param publicKey 公钥
     * @return 验证结果
     */
    public static boolean verifySignature(String data, String sign, String publicKey,String algorithm) throws Exception {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(getPublicKey(publicKey));
        signature.update(data.getBytes());
        return signature.verify(HexUtil.decodeHex(sign));
    }

    /**
     * 获取公钥
     * @param publicKey 公钥字符串
     * @return PublicKey对象
     */
    private static PublicKey getPublicKey(String publicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}
