package com.lockulockme.locku.base.utils;



import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @CreateDate: 2020/11/2 10:48
 */
public class EncryptUtil {
    private static final String transformation="AES/ECB/PKCS5Padding";
    private static final String algorithm="AES";

    public static byte[] encrypt(String transformation, String algorithm, byte[] content, String key) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.US_ASCII), algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(content);
        } catch (Exception e) {
            LogUtil.LogE("EncryptUtil", e.getMessage());
            return null;
        }
    }

    public static byte[] encrypt(byte[] content, String key) {
        return encrypt(transformation,algorithm,content,key);
    }

    public static byte[] decrypt(String transformation, String algorithm, byte[] content, String key) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.US_ASCII), algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(content);
        } catch (Exception e) {
            LogUtil.LogE("EncryptUtil", e.getMessage());
            return null;
        }
    }

    public static byte[] decrypt(byte[] content, String key) {
        return decrypt(transformation,algorithm,content,key);
    }
}
